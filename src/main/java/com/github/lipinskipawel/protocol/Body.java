package com.github.lipinskipawel.protocol;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Optional;

public record Body(
        @JsonProperty("type") String type,
        @JsonProperty("msg_id") int msgId,
        @JsonProperty("in_reply_to") Optional<Integer> inReplyTo,
        @JsonProperty("echo") String echo
) {
}
