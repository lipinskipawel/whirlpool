package com.github.lipinskipawel.workload.broadcast;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.github.lipinskipawel.maelstrom.protocol.CustomRequest;
import io.github.lipinskipawel.maelstrom.protocol.EventType;

import java.util.List;

public final class Internal extends EventType implements CustomRequest {
    @JsonProperty("messages_from_other_node")
    public List<Integer> messagesFromOtherNode;

    public Internal() {
        super("internal");
    }

    public Internal(List<Integer> messagesFromOtherNode) {
        super("internal");
        this.messagesFromOtherNode = messagesFromOtherNode;
    }
}
