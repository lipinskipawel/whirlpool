package com.github.lipinskipawel.workload;

import com.fasterxml.jackson.core.type.TypeReference;
import com.github.lipinskipawel.protocol.Message;
import com.github.lipinskipawel.protocol.UniqueBody;

import static com.github.lipinskipawel.protocol.Json.toJson;
import static com.github.lipinskipawel.protocol.Json.toObject;
import static com.github.lipinskipawel.protocol.Message.messageWithUniqueBody;
import static java.util.UUID.randomUUID;

/**
 * Run unique-ids maelstrom workload
 * ./maelstrom test -w unique-ids --bin whirlpool.sh --time-limit 30 --rate 1000 --node-count 3 --availability total --nemesis partition
 */
public final class UniqueResponder {
    private int msgCounter;

    public UniqueResponder() {
        this.msgCounter = 0;
    }

    public String handle(String request) {
        final var uniqueMessage = toObject(request, new TypeReference<Message<UniqueBody>>() {
        });

        return toJson(messageWithUniqueBody(uniqueMessage.dst(), uniqueMessage.src(), body -> body
                .withType("generate_ok")
                .withMsgId(msgCounter++)
                .withInReplyTo(uniqueMessage.body().msgId())
                .withId(randomUUID())
        ));
    }
}
