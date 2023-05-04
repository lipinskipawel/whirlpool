package com.github.lipinskipawel.workload;

import com.fasterxml.jackson.core.type.TypeReference;
import com.github.lipinskipawel.protocol.BroadcastBody;
import com.github.lipinskipawel.protocol.Json;
import com.github.lipinskipawel.protocol.Message;
import com.github.lipinskipawel.protocol.ReadBody;
import com.github.lipinskipawel.protocol.TopologyBody;

import java.util.ArrayList;
import java.util.List;

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
 */
public final class BroadcastResponder {
    private final String nodeId;
    private final List<String> restOfTheNodes;
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
        this.restOfTheNodes = allNodeIds.stream().filter(not(it -> it.equals(nodeId))).toList();
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
        broadcastMessage.body().message().ifPresent(this.messages::add);

        this.restOfTheNodes
                .stream()
                .map(it -> messageWithBroadcastBody(nodeId, it, body -> body
                        .withType("internal")
                        .withMessagesFromOtherNode(this.messages)))
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

        final var messages = List.copyOf(this.messages);
        System.out.println(Json.toJson(messageWithReadBody(readMessage.dst(), readMessage.src(), body -> body
                .withType("read_ok")
                .withMessages(messages)
                .withMsgId(readCounter++)
                .withInReplyTo(readMessage.body().msgId().get())
        )));
    }

    private void topology(String request) {
        final var topologyMessage = Json.toObject(request, new TypeReference<Message<TopologyBody>>() {
        });

        System.out.println(Json.toJson(messageWithTopologyBody(topologyMessage.dst(), topologyMessage.src(), body -> body
                .withType("topology_ok")
                .withMsgId(topologyCounter++)
                .withInReplyTo(topologyMessage.body().msgId().get())
        )));
    }

    private void internal(String request) {
        final var internalMessage = Json.toObject(request, new TypeReference<Message<BroadcastBody>>() {
        });
        if (internalMessage.src().equals(nodeId)) {
            return;
        }

        this.messages.clear();
        internalMessage.body().messagesFromOtherNode().ifPresent(this.messages::addAll);
    }
}
