package com.github.lipinskipawel.framework1;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.github.lipinskipawel.base.CustomRequest;
import com.github.lipinskipawel.base.EventType;

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
