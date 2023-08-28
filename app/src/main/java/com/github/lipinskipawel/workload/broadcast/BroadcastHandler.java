package com.github.lipinskipawel.workload.broadcast;

import com.github.lipinskipawel.whirlpool.protocol.Broadcast;
import com.github.lipinskipawel.whirlpool.protocol.BroadcastOk;
import com.github.lipinskipawel.whirlpool.protocol.BroadcastWorkload;
import com.github.lipinskipawel.whirlpool.protocol.CustomRequest;
import com.github.lipinskipawel.whirlpool.protocol.Init;
import com.github.lipinskipawel.whirlpool.protocol.InitOk;
import com.github.lipinskipawel.whirlpool.protocol.Quit;
import com.github.lipinskipawel.whirlpool.protocol.Read;
import com.github.lipinskipawel.whirlpool.protocol.ReadOk;
import com.github.lipinskipawel.whirlpool.protocol.Topology;
import com.github.lipinskipawel.whirlpool.protocol.TopologyOk;
import com.github.lipinskipawel.whirlpool.framework.Event;
import com.github.lipinskipawel.whirlpool.framework.EventHandler;

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

import static com.github.lipinskipawel.whirlpool.framework.Event.createEvent;
import static java.util.concurrent.Executors.newSingleThreadScheduledExecutor;
import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.function.Predicate.not;

public final class BroadcastHandler extends EventHandler<BroadcastWorkload> {
    private final AtomicReference<String> nodeId;
    private List<String> reachableNodes;
    private final Set<Integer> messages;
    private final Map<String, Set<Integer>> known;
    private final ScheduledExecutorService schedule;
    private final ThreadLocalRandom random;

    public BroadcastHandler() {
        this.nodeId = new AtomicReference<>();
        this.reachableNodes = new CopyOnWriteArrayList<>();
        this.messages = new CopyOnWriteArraySet<>();
        this.known = new ConcurrentHashMap<>();
        this.schedule = newSingleThreadScheduledExecutor();
        this.schedule.scheduleAtFixedRate(this::gossip, 200, 200, MILLISECONDS);
        this.random = ThreadLocalRandom.current();
    }

    @Override
    public void handle(Event<BroadcastWorkload> event) {
        final var body = event.body;
        if (body instanceof Init init) {
            this.nodeId.set(init.nodeId);
            this.reachableNodes.addAll(init.nodeIds.stream().filter(not(it -> it.equals(nodeId.get()))).toList());
            replyAndSend(event, new InitOk());
            return;
        }
        if (body instanceof Broadcast broadcast) {
            this.messages.add(broadcast.message);
            replyAndSend(event, new BroadcastOk());
            return;
        }
        if (body instanceof Read) {
            replyAndSend(event, new ReadOk(this.messages.stream().toList()));
            return;
        }
        if (body instanceof Topology topology) {
            this.reachableNodes = new CopyOnWriteArrayList<>(topology.topology.get(this.nodeId.get()));
            this.reachableNodes.forEach(it -> this.known.put(it, new CopyOnWriteArraySet<>()));
            replyAndSend(event, new TopologyOk());
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
            try {
                Thread.sleep(900);
                this.schedule.shutdownNow();
                this.schedule.awaitTermination(500, MILLISECONDS);
            } catch (InterruptedException e) {
                System.err.println("Exception during shutdown of: " + getClass());
                System.err.println(e);
            }
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
                    return createEvent(0, nodeId.get(), it, new Internal(extended));
                })
                .filter(not(it -> it.body.messagesFromOtherNode.size() == 0))
                .forEach(this::send);
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
