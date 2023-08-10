package com.github.lipinskipawel.framework2.protocol;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.github.lipinskipawel.framework2.Body;

import java.util.List;

public final class ReadOk extends Body {
    @JsonProperty("messages")
    public List<Integer> messages;

    public ReadOk() {
    }

    public ReadOk(List<Integer> messages) {
        this.messages = messages;
    }
}
