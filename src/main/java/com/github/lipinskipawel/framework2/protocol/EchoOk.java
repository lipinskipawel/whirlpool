package com.github.lipinskipawel.framework2.protocol;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.github.lipinskipawel.framework2.Body;

public final class EchoOk extends Body {
    @JsonProperty("echo")
    public String echo;

    public EchoOk() {
    }

    public EchoOk(String echo) {
        this.echo = echo;
    }
}
