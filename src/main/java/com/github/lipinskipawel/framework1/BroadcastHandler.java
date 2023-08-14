package com.github.lipinskipawel.framework1;

import com.github.lipinskipawel.base.Broadcast;
import com.github.lipinskipawel.base.BroadcastOk;
import com.github.lipinskipawel.base.BroadcastWorkload;
import com.github.lipinskipawel.base.CustomRequest;
import com.github.lipinskipawel.base.Init;
import com.github.lipinskipawel.base.InitOk;
import com.github.lipinskipawel.base.Quit;
import com.github.lipinskipawel.base.Read;
import com.github.lipinskipawel.base.ReadOk;
import com.github.lipinskipawel.base.Topology;
import com.github.lipinskipawel.base.TopologyOk;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Stream;

import static com.github.lipinskipawel.framework1.Server.writeRequest;
import static java.util.concurrent.Executors.newSingleThreadScheduledExecutor;
import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.function.Predicate.not;

final class BroadcastHandler {
    private final AtomicReference<String> nodeId;
    private List<String> reachableNodes;
    private final Set<Integer> messages;
    private final Map<String, Set<Integer>> known;
    private final ScheduledExecutorService schedule;
    private final ThreadLocalRandom random;

    BroadcastHandler() {
        this.nodeId = new AtomicReference<>();
        this.reachableNodes = new CopyOnWriteArrayList<>();
        this.messages = new CopyOnWriteArraySet<>();
        this.known = new ConcurrentHashMap<>();
        this.schedule = newSingleThreadScheduledExecutor();
        this.schedule.scheduleAtFixedRate(this::gossip, 200, 200, MILLISECONDS);
        this.random = ThreadLocalRandom.current();
    }

    void handle(Event<?> event) throws InterruptedException {
        final var body = (BroadcastWorkload) event.body;
        if (body instanceof Init init) {
            this.nodeId.set(init.nodeId);
            this.reachableNodes.addAll(init.nodeIds.stream().filter(not(it -> it.equals(nodeId.get()))).toList());
            final var response = new Event<>(new InitOk());
            response.src = event.dst;
            response.dst = event.src;
            response.body.msgId = 1;
            response.body.inReplyTo = init.msgId;
            System.out.println(writeRequest(response));
            return;
        }
        if (body instanceof Broadcast broadcast) {
            this.messages.add(broadcast.message);
            final var response = new Event<>(new BroadcastOk());
            response.src = event.dst;
            response.dst = event.src;
            response.body.msgId = broadcast.msgId + 1;
            response.body.inReplyTo = broadcast.msgId;
            System.out.println(writeRequest(response));
            return;
        }
        if (body instanceof Read read) {
            final var response = new Event<>(new ReadOk(this.messages.stream().toList()));
            response.src = event.dst;
            response.dst = event.src;
            response.body.msgId = read.msgId + 1;
            response.body.inReplyTo = read.msgId;
            System.out.println(writeRequest(response));
            return;
        }
        if (body instanceof Topology topology) {
            this.reachableNodes = new CopyOnWriteArrayList<>(topology.topology.get(this.nodeId.get()));
            this.reachableNodes.forEach(it -> this.known.put(it, new CopyOnWriteArraySet<>()));
            final var response = new Event<>(new TopologyOk());
            response.src = event.dst;
            response.dst = event.src;
            response.body.msgId = topology.msgId + 1;
            response.body.inReplyTo = topology.msgId;
            System.out.println(writeRequest(response));
            return;
        }
        if (body instanceof CustomRequest customRequest) {
            if (customRequest instanceof Internal internal) {
                this.known.computeIfPresent(event.src, (k, v) -> {
                    v.addAll(internal.messagesFromOtherNode);
                    return v;
                });
                this.messages.addAll(internal.messagesFromOtherNode);
                return;
            }
        }
        if (body instanceof Quit) {
            Thread.sleep(900);
            this.schedule.shutdownNow();
            this.schedule.awaitTermination(500, MILLISECONDS);
        }
    }

    private void gossip() {
        this.reachableNodes
                .stream()
                .map(it -> {
                    final var toSend = this.messages
                            .stream()
                            .filter(not(message -> this.known.get(it).contains(message)))
                            .toList();
                    final var extended = extendedByKnownMessage(it, toSend);
                    final var event = new Event<>(new Internal(extended));
                    event.src = nodeId.get();
                    event.dst = it;
                    return event;
                })
                .filter(not(it -> it.body.messagesFromOtherNode.size() == 0))
                .map(Server::writeRequest)
                .forEach(System.out::println);
    }

    private List<Integer> extendedByKnownMessage(String nodeId, List<Integer> toSend) {
        try {
            if (toSend.isEmpty()) {
                return toSend;
            }
            final int numberOfExtraToSend = (10 * toSend.size() / 100);
            final var knownToTarget = this.known.get(nodeId);
            final var additional = this.messages
                    .stream()
                    .filter(knownToTarget::contains)
                    .filter(it -> random.nextInt(toSend.size()) < numberOfExtraToSend)
                    .toList();
            return Stream.of(toSend, additional).flatMap(List::stream).toList();
        } catch (RuntimeException ee) {
            debug("GOSSIP - RuntimeException - %s%n".formatted(ee));
            return toSend;
        }
    }

    private static void debug(String debug) {
        System.err.println(debug);
    }
}
