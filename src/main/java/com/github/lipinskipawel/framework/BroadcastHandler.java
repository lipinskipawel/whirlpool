package com.github.lipinskipawel.framework;

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

import static com.github.lipinskipawel.framework.FrameworkMessage.Builder.frameworkMessage;
import static com.github.lipinskipawel.framework.FrameworkMessage.replyToRead;
import static com.github.lipinskipawel.framework.FrameworkMessage.replyToTopology;
import static java.util.Collections.emptyList;
import static java.util.concurrent.Executors.newSingleThreadScheduledExecutor;
import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.function.Predicate.not;

/**
 * Run broadcast 3a maelstrom workload
 * ./maelstrom test -w broadcast --bin whirlpool.sh --node-count 1 --time-limit 20 --rate 10
 * <p>
 * Run broadcast 3b maelstrom workload
 * ./maelstrom test -w broadcast --bin whirlpool.sh --node-count 5 --time-limit 20 --rate 10
 * <p>
 * Run broadcast 3c maelstrom workload
 * ./maelstrom test -w broadcast --bin whirlpool.sh --node-count 5 --time-limit 20 --rate 10 --nemesis partition
 * <p>
 * Run maelstrom 3d/3e maelstrom workload
 * ./maelstrom test -w broadcast --bin whirlpool.sh --node-count 25 --time-limit 20 --rate 100 --latency 100
 */
public final class BroadcastHandler extends RequestHandler<FrameworkMessage<Broadcast>> {
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
    public void init(String nodeId, List<String> nodesIds) {
        this.nodeId.set(nodeId);
        this.reachableNodes.addAll(nodesIds.stream().filter(not(it -> it.equals(nodeId))).toList());
    }

    @Override
    public void handle(FrameworkMessage<Broadcast> message) {
        messagesToSend(message).forEach(this::send);
    }

    private List<FrameworkMessage<?>> messagesToSend(FrameworkMessage<Broadcast> message) {
        final var type = message.getBody().type();
        return switch (type) {
            case "broadcast" -> broadcast(message);
            case "read" -> read(message);
            case "topology" -> topology(message);
            case "internal" -> internal(message);
            default -> throw new RuntimeException("Type %s is not supported".formatted(type));
        };
    }

    @Override
    public void quit() {
        try {
            Thread.sleep(900);
            this.schedule.shutdownNow();
            this.schedule.awaitTermination(500, MILLISECONDS);
        } catch (InterruptedException e) {
            debug("Interrupted: " + Thread.currentThread().getName());
            Thread.currentThread().interrupt();
        }
    }

    private List<FrameworkMessage<?>> broadcast(FrameworkMessage<Broadcast> request) {
        this.messages.add(request.getBody().message().get());

        return List.of(request.reply(frameworkMessage()
                .withBody(Broadcast.broadcast("broadcast_ok").build())
                .build()));
    }

    private List<FrameworkMessage<?>> read(FrameworkMessage<Broadcast> request) {
        return List.of(replyToRead(request, this.messages.stream().toList()));
    }

    private List<FrameworkMessage<?>> topology(FrameworkMessage<Broadcast> request) {
        this.reachableNodes = new CopyOnWriteArrayList<>(request.getBody().topology().get(nodeId.get()));
        this.reachableNodes.forEach(it -> this.known.put(it, new CopyOnWriteArraySet<>()));

        return List.of(replyToTopology(request));
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
                    return frameworkMessage()
                            .withSrc(nodeId.get())
                            .withDst(it)
                            .withBody(Broadcast.broadcast("internal")
                                    .withMessagesFromOtherNodes(extended)
                                    .build())
                            .withType("internal")
                            .<Broadcast>build();
                })
                .filter(not(it -> it.getBody().messagesFromOtherNodes().size() == 0))
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

    private List<FrameworkMessage<?>> internal(FrameworkMessage<Broadcast> request) {
        this.known.computeIfPresent(request.getSrc(), (k, v) -> {
            v.addAll(request.getBody().messagesFromOtherNodes());
            return v;
        });
        this.messages.addAll(request.getBody().messagesFromOtherNodes());
        return emptyList();
    }
}
