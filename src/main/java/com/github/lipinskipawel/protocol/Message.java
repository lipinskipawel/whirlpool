package com.github.lipinskipawel.protocol;

import com.fasterxml.jackson.annotation.JsonProperty;

public record Message<T>(
        @JsonProperty("src") String src,
        @JsonProperty("dest") String dst,
        @JsonProperty("body") T body
) {
}
