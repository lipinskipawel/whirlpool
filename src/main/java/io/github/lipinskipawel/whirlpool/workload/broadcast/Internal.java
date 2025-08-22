package io.github.lipinskipawel.whirlpool.workload.broadcast;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.github.lipinskipawel.maelstrom.api.protocol.EventType;
import io.github.lipinskipawel.maelstrom.spi.protocol.CustomEvent;

import java.util.List;

public final class Internal extends EventType implements CustomEvent {
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
