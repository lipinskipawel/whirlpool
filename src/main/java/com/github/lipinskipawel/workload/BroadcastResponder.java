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

/**
 * Run broadcast 3a maelstrom workload
 * ./maelstrom test -w broadcast --bin whirlpool.sh --node-count 1 --time-limit 20 --rate 10
 */
public final class BroadcastResponder {
    private final List<Integer> messages;
    private int broadcastCounter;
    private int readCounter;
    private int topologyCounter;

    private enum BroadcastTypes {
        BROADCAST,
        READ,
        TOPOLOGY
    }

    public BroadcastResponder() {
        this.messages = new ArrayList<>();
        this.broadcastCounter = 0;
        this.readCounter = 0;
        this.topologyCounter = 0;
    }

    public String handle(String request) {
        final var typeOfBroadcast = BroadcastTypes.valueOf(Json.typeOfJson(request).toUpperCase());
        return switch (typeOfBroadcast) {
            case BROADCAST -> broadcast(request);
            case READ -> read(request);
            case TOPOLOGY -> topology(request);
        };
    }

    private String broadcast(String request) {
        final var broadcastMessage = Json.toObject(request, new TypeReference<Message<BroadcastBody>>() {
        });
        broadcastMessage.body().message().ifPresent(this.messages::add);

        return Json.toJson(messageWithBroadcastBody(broadcastMessage.dst(), broadcastMessage.src(), body -> body
                .withType("broadcast_ok")
                .withMsgId(broadcastCounter++)
                .withInReplyTo(broadcastMessage.body().msgId().get())
        ));
    }

    private String read(String request) {
        final var readMessage = Json.toObject(request, new TypeReference<Message<ReadBody>>() {
        });

        final var messages = List.copyOf(this.messages);
        return Json.toJson(messageWithReadBody(readMessage.dst(), readMessage.src(), body -> body
                .withType("read_ok")
                .withMessages(messages)
                .withMsgId(readCounter++)
                .withInReplyTo(readMessage.body().msgId().get())
        ));
    }

    private String topology(String request) {
        final var topologyMessage = Json.toObject(request, new TypeReference<Message<TopologyBody>>() {
        });

        return Json.toJson(messageWithTopologyBody(topologyMessage.dst(), topologyMessage.src(), body -> body
                .withType("topology_ok")
                .withMsgId(topologyCounter++)
                .withInReplyTo(topologyMessage.body().msgId().get())
        ));
    }
}
