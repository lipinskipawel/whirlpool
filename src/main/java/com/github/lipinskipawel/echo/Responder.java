package com.github.lipinskipawel.echo;

import com.github.lipinskipawel.protocol.EchoBody;
import com.github.lipinskipawel.protocol.Message;

import java.util.List;

import static com.github.lipinskipawel.protocol.Json.toJson;
import static com.github.lipinskipawel.protocol.Message.messageWithBody;

public final class Responder {
    private final String nodeId;
    private final List<String> nodeIds;
    private int msgCounter;

    public Responder(String nodeId, List<String> nodeIds) {
        this.nodeId = nodeId;
        this.nodeIds = nodeIds;
        this.msgCounter = 0;
    }

    public String handle(Message<EchoBody> initMessage) {
        return toJson(messageWithBody(initMessage.dst(), initMessage.src(), body -> body
                .withType("echo_ok")
                .withMsgId(msgCounter++)
                .withInReplyTo(initMessage.body().msgId())
                .withEcho(initMessage.body().echo()))
        );
    }
}
