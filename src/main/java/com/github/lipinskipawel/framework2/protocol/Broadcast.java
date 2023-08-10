package com.github.lipinskipawel.framework2.protocol;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.github.lipinskipawel.framework2.Body;

public final class Broadcast extends Body {
    @JsonProperty("message")
    public Integer message;

    public Broadcast() {
        super();
    }

    public Broadcast(Integer message) {
        this.message = message;
    }
}
