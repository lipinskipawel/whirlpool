package com.github.lipinskipawel.workload;

import com.fasterxml.jackson.core.type.TypeReference;
import com.github.lipinskipawel.protocol.BroadcastBody;
import com.github.lipinskipawel.protocol.Json;
import com.github.lipinskipawel.protocol.Message;
import com.github.lipinskipawel.protocol.ReadBody;
import com.github.lipinskipawel.protocol.TopologyBody;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Stream;

import static com.github.lipinskipawel.protocol.Message.messageWithBroadcastBody;
import static com.github.lipinskipawel.protocol.Message.messageWithReadBody;
import static com.github.lipinskipawel.protocol.Message.messageWithTopologyBody;
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
public final class BroadcastResponder {
    private final String nodeId;
    private final Set<Integer> messages;
    private final Map<String, Set<Integer>> known;
    private List<String> reachableNodes;
    private final ScheduledExecutorService schedule;
    private final ThreadLocalRandom random;
    private int broadcastCounter;
    private int readCounter;
    private int topologyCounter;

    private enum BroadcastTypes {
        BROADCAST,
        READ,
        TOPOLOGY,
        INTERNAL
    }

    public BroadcastResponder(String nodeId, List<String> allNodeIds) {
        this.nodeId = nodeId;
        this.reachableNodes = allNodeIds.stream().filter(not(it -> it.equals(nodeId))).toList();
        this.messages = new CopyOnWriteArraySet<>();
        this.known = new ConcurrentHashMap<>();
        this.schedule = newSingleThreadScheduledExecutor();
        this.schedule.scheduleAtFixedRate(this::gossip, 200, 200, MILLISECONDS);
        this.random = ThreadLocalRandom.current();
        this.broadcastCounter = 0;
        this.readCounter = 0;
        this.topologyCounter = 0;
    }

    public void handle(String request) throws InterruptedException {
        if (request.equals("quit")) {
            Thread.sleep(900);
            this.schedule.shutdownNow();
            this.schedule.awaitTermination(500, MILLISECONDS);
            return;
        }
        final var typeOfBroadcast = BroadcastTypes.valueOf(Json.typeOfJson(request).toUpperCase());
        switch (typeOfBroadcast) {
            case BROADCAST -> broadcast(request);
            case READ -> read(request);
            case TOPOLOGY -> topology(request);
            case INTERNAL -> internal(request);
        }
    }

    private void broadcast(String request) {
        final var broadcastMessage = Json.toObject(request, new TypeReference<Message<BroadcastBody>>() {
        });
        broadcastMessage.body().message().ifPresent(this.messages::add);

        System.out.println(Json.toJson(messageWithBroadcastBody(broadcastMessage.dst(), broadcastMessage.src(), body -> body
                .withType("broadcast_ok")
                .withMsgId(broadcastCounter++)
                .withInReplyTo(broadcastMessage.body().msgId().get())
        )));
    }

    private void read(String request) {
        final var readMessage = Json.toObject(request, new TypeReference<Message<ReadBody>>() {
        });

        System.out.println(Json.toJson(messageWithReadBody(readMessage.dst(), readMessage.src(), body -> body
                .withType("read_ok")
                .withMessages(this.messages.stream().toList())
                .withMsgId(readCounter++)
                .withInReplyTo(readMessage.body().msgId().get())
        )));
    }

    private void topology(String request) {
        final var topologyMessage = Json.toObject(request, new TypeReference<Message<TopologyBody>>() {
        });

        topologyMessage.body().topology().ifPresent(topology -> this.reachableNodes = topology.get(nodeId));
        this.reachableNodes.forEach(it -> this.known.put(it, new CopyOnWriteArraySet<>()));

        System.out.println(Json.toJson(messageWithTopologyBody(topologyMessage.dst(), topologyMessage.src(), body -> body
                .withType("topology_ok")
                .withMsgId(topologyCounter++)
                .withInReplyTo(topologyMessage.body().msgId().get())
        )));
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
                    return messageWithBroadcastBody(nodeId, it, body -> body
                            .withType("internal")
                            .withMessagesFromOtherNode(extended));
                })
                .filter(not(it -> it.body().messagesFromOtherNode().get().size() == 0))
                .map(Json::toJson)
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
            System.err.printf("GOSSIP - RuntimeException - %s%n", ee);
            return toSend;
        }
    }

    private void internal(String request) {
        final var internalMessage = Json.toObject(request, new TypeReference<Message<BroadcastBody>>() {
        });
        this.known.computeIfPresent(internalMessage.src(), (k, v) -> {
            internalMessage.body().messagesFromOtherNode().ifPresent(v::addAll);
            return v;
        });
        internalMessage.body().messagesFromOtherNode().ifPresent(this.messages::addAll);
    }
}
