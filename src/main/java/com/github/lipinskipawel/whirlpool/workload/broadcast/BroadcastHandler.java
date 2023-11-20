package com.github.lipinskipawel.whirlpool.workload.broadcast;

import com.github.lipinskipawel.maelstrom.api.framework.EventHandler;
import com.github.lipinskipawel.maelstrom.api.protocol.Event;
import com.github.lipinskipawel.maelstrom.api.protocol.Init;
import com.github.lipinskipawel.maelstrom.api.protocol.InitOk;
import com.github.lipinskipawel.maelstrom.api.protocol.Quit;
import com.github.lipinskipawel.maelstrom.api.protocol.broadcast.Broadcast;
import com.github.lipinskipawel.maelstrom.api.protocol.broadcast.BroadcastOk;
import com.github.lipinskipawel.maelstrom.api.protocol.broadcast.BroadcastWorkload;
import com.github.lipinskipawel.maelstrom.api.protocol.broadcast.Read;
import com.github.lipinskipawel.maelstrom.api.protocol.broadcast.ReadOk;
import com.github.lipinskipawel.maelstrom.api.protocol.broadcast.Topology;
import com.github.lipinskipawel.maelstrom.api.protocol.broadcast.TopologyOk;
import com.github.lipinskipawel.maelstrom.spi.protocol.CustomEvent;

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

import static com.github.lipinskipawel.maelstrom.api.protocol.Event.createEvent;
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
            case CustomEvent customEvent -> customRequestEvent(customEvent, event);
            case Quit __ -> quitEvent();
        }
    }

    private void initEvent(Init init, Event<BroadcastWorkload> event) {
        this.nodeId.set(init.nodeId);
        this.reachableNodes.addAll(init.nodeIds.stream().filter(not(it -> it.equals(nodeId.get()))).toList());
        send(event.reply(new InitOk()));
    }

    private void broadcastEvent(Broadcast broadcast, Event<BroadcastWorkload> event) {
        this.messages.add(broadcast.message);
        send(event.reply(new BroadcastOk()));
    }

    private void readEvent(Event<BroadcastWorkload> event) {
        send(event.reply(new ReadOk(this.messages.stream().toList())));
    }

    private void topologyEvent(Topology topology, Event<BroadcastWorkload> event) {
        this.reachableNodes = new CopyOnWriteArrayList<>(topology.topology.get(this.nodeId.get()));
        this.reachableNodes.forEach(it -> this.known.put(it, new CopyOnWriteArraySet<>()));
        send(event.reply(new TopologyOk()));
    }

    private void customRequestEvent(CustomEvent customEvent, Event<BroadcastWorkload> event) {
        if (customEvent instanceof Internal internal) {
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
