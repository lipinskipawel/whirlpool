package com.github.lipinskipawel.protocol;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Optional;

public record InitBody(
        @JsonProperty("type") String type,
        @JsonProperty("msg_id") Optional<Integer> msgId,
        @JsonProperty("in_reply_to") Optional<Integer> inReplyTo,
        @JsonProperty("node_id") Optional<String> nodeId,
        @JsonProperty("node_ids") Optional<List<String>> nodeIds
) {
}
