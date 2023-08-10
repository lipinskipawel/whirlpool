package com.github.lipinskipawel.framework2.protocol;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.github.lipinskipawel.framework2.Body;

import java.util.List;

public final class Internal extends Body {
    @JsonProperty("messages_from_other_node")
    public List<Integer> messagesFromOtherNode;

    public Internal() {
    }

    public Internal(List<Integer> messagesFromOtherNode) {
        this.messagesFromOtherNode = messagesFromOtherNode;
    }
}
