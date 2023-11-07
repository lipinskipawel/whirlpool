package com.github.lipinskipawel.workload.broadcast;

import io.github.lipinskipawel.maelstrom.framework.Event;
import io.github.lipinskipawel.maelstrom.framework.EventHandler;
import io.github.lipinskipawel.maelstrom.protocol.CustomRequest;
import io.github.lipinskipawel.maelstrom.protocol.Init;
import io.github.lipinskipawel.maelstrom.protocol.InitOk;
import io.github.lipinskipawel.maelstrom.protocol.Quit;
import io.github.lipinskipawel.maelstrom.protocol.broadcast.Broadcast;
import io.github.lipinskipawel.maelstrom.protocol.broadcast.BroadcastOk;
import io.github.lipinskipawel.maelstrom.protocol.broadcast.BroadcastWorkload;
import io.github.lipinskipawel.maelstrom.protocol.broadcast.Read;
import io.github.lipinskipawel.maelstrom.protocol.broadcast.ReadOk;
import io.github.lipinskipawel.maelstrom.protocol.broadcast.Topology;
import io.github.lipinskipawel.maelstrom.protocol.broadcast.TopologyOk;

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

import static io.github.lipinskipawel.maelstrom.framework.Event.createEvent;
import static java.util.concurrent.Executors.newSingleThreadScheduledExecutor;
import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.function.Predicate.not;

/**
 * ./maelstrom test -w broadcast --bin whirlpool/whirlpool.sh --node-count 5 --time-limit 20 --rate 10 \
 *     --nemesis partition --log-stderr
 */
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
        switch (event.body) {
            case Init init -> initEvent(init, event);
            case Broadcast broadcast -> broadcastEvent(broadcast, event);
            case Read __ -> readEvent(event);
            case Topology topology -> topologyEvent(topology, event);
            case CustomRequest customRequest -> customRequestEvent(customRequest, event);
            case Quit __ -> quitEvent();
        }
    }

    private void initEvent(Init init, Event<BroadcastWorkload> event) {
        this.nodeId.set(init.nodeId);
        this.reachableNodes.addAll(init.nodeIds.stream().filter(not(it -> it.equals(nodeId.get()))).toList());
        replyAndSend(event, new InitOk());
    }

    private void broadcastEvent(Broadcast broadcast, Event<BroadcastWorkload> event) {
        this.messages.add(broadcast.message);
        replyAndSend(event, new BroadcastOk());
    }

    private void readEvent(Event<BroadcastWorkload> event) {
        replyAndSend(event, new ReadOk(this.messages.stream().toList()));
    }

    private void topologyEvent(Topology topology, Event<BroadcastWorkload> event) {
        this.reachableNodes = new CopyOnWriteArrayList<>(topology.topology.get(this.nodeId.get()));
        this.reachableNodes.forEach(it -> this.known.put(it, new CopyOnWriteArraySet<>()));
        replyAndSend(event, new TopologyOk());
    }

    private void customRequestEvent(CustomRequest customRequest, Event<BroadcastWorkload> event) {
        if (customRequest instanceof Internal internal) {
            this.known.computeIfPresent(event.src, (k, v) -> {
                v.addAll(internal.messagesFromOtherNode);
                return v;
            });
            this.messages.addAll(internal.messagesFromOtherNode);
        }
    }

    private void quitEvent() {
        try {
            Thread.sleep(900);
            this.schedule.shutdownNow();
            this.schedule.awaitTermination(500, MILLISECONDS);
        } catch (InterruptedException e) {
            System.err.println("Exception during shutdown of: " + getClass());
            System.err.println(e);
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
                .filter(not(it -> it.body.messagesFromOtherNode.isEmpty()))
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
