package com.github.lipinskipawel.workload;

import com.fasterxml.jackson.core.type.TypeReference;
import com.github.lipinskipawel.protocol.BroadcastBody;
import com.github.lipinskipawel.protocol.Json;
import com.github.lipinskipawel.protocol.Message;
import com.github.lipinskipawel.protocol.ReadBody;
import com.github.lipinskipawel.protocol.TopologyBody;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import static com.github.lipinskipawel.protocol.Message.messageWithBroadcastBody;
import static com.github.lipinskipawel.protocol.Message.messageWithReadBody;
import static com.github.lipinskipawel.protocol.Message.messageWithTopologyBody;
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
 * Run maelstrom 3d maelstrom workload
 * ./maelstrom test -w broadcast --bin whirlpool.sh --node-count 25 --time-limit 20 --rate 100 --latency 100
 */
public final class BroadcastResponder {
    private final String nodeId;
    private List<String> reachableNodes;
    private final List<Integer> messages;
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
        this.messages = new ArrayList<>();
        this.broadcastCounter = 0;
        this.readCounter = 0;
        this.topologyCounter = 0;
    }

    public void handle(String request) {
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
        broadcastMessage.body().message().ifPresent(value -> {
            if (!this.messages.contains(value)) {
                this.messages.add(value);
            }
        });

        this.reachableNodes
                .stream()
                .map(it -> messageWithBroadcastBody(nodeId, it, body -> body
                        .withType("internal")
                        .withMessagesFromOtherNode(List.of(broadcastMessage.body().message().get()))
                        .withVisitedNodes(List.of(nodeId))))
                .map(Json::toJson)
                .forEach(System.out::println);

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
                .withMessages(this.messages)
                .withMsgId(readCounter++)
                .withInReplyTo(readMessage.body().msgId().get())
        )));
    }

    private void topology(String request) {
        final var topologyMessage = Json.toObject(request, new TypeReference<Message<TopologyBody>>() {
        });

        topologyMessage.body().topology().ifPresent(topology -> this.reachableNodes = topology.get(nodeId));

        System.out.println(Json.toJson(messageWithTopologyBody(topologyMessage.dst(), topologyMessage.src(), body -> body
                .withType("topology_ok")
                .withMsgId(topologyCounter++)
                .withInReplyTo(topologyMessage.body().msgId().get())
        )));
    }

    private void internal(String request) {
        final var internalMessage = Json.toObject(request, new TypeReference<Message<BroadcastBody>>() {
        });
        if (internalMessage.body().visitedNodes().get().contains(nodeId)) {
            return;
        }
        final var messagesFromOtherNode = internalMessage.body().messagesFromOtherNode().orElseGet(List::of);
        if (messagesFromOtherNode.size() == 0) {
            return;
        }
        final var difference = difference(this.messages, internalMessage.body().messagesFromOtherNode().get());
        if (difference.size() == 0) {
            return;
        }
        this.messages.addAll(difference);

        this.reachableNodes
                .stream()
                .filter(not(it -> internalMessage.body().visitedNodes().get().contains(it)))
                .map(it -> {
                    final var randomUpTo1 = ThreadLocalRandom.current().nextFloat(1);
                    final var visitedNodes = new ArrayList<>(internalMessage.body().visitedNodes().get());
                    visitedNodes.add(nodeId);
                    return messageWithBroadcastBody(nodeId, it, body -> body
                            .withType("internal")
                            .withMessagesFromOtherNode(randomUpTo1 > 0.2 ? difference.stream().toList() : this.messages)
                            .withVisitedNodes(visitedNodes)
                    );
                })
                .map(Json::toJson)
                .forEach(System.out::println);
    }

    private List<Integer> difference(List<Integer> first, List<Integer> second) {
        return second
                .stream()
                .filter(not(first::contains))
                .toList();
    }
}
