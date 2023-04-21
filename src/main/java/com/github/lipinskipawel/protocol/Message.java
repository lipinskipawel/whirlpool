package com.github.lipinskipawel.protocol;

import com.fasterxml.jackson.annotation.JsonProperty;

public record Message(
        @JsonProperty("src") String src,
        @JsonProperty("dest") String dst,
        @JsonProperty("body") Body body
) {
}
