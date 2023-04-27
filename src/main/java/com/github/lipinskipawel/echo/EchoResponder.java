package com.github.lipinskipawel.echo;

import com.fasterxml.jackson.core.type.TypeReference;
import com.github.lipinskipawel.protocol.EchoBody;
import com.github.lipinskipawel.protocol.Json;
import com.github.lipinskipawel.protocol.Message;

import static com.github.lipinskipawel.protocol.Json.toJson;
import static com.github.lipinskipawel.protocol.Message.messageWithBody;

/**
 * Run echo maelstrom test
 * ./maelstrom test -w echo --bin whirlpool.sh --node-count 1 --time-limit 10 --log-stderr
 */
public final class EchoResponder {
    private int msgCounter;

    public EchoResponder() {
        this.msgCounter = 0;
    }

    public String handle(String request) {
        final var initMessage = Json.toObject(request, new TypeReference<Message<EchoBody>>() {
        });

        return toJson(messageWithBody(initMessage.dst(), initMessage.src(), body -> body
                .withType("echo_ok")
                .withMsgId(msgCounter++)
                .withInReplyTo(initMessage.body().msgId())
                .withEcho(initMessage.body().echo()))
        );
    }
}
