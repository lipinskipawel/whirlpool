package com.github.lipinskipawel.framework2.protocol;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.github.lipinskipawel.framework2.Body;

public final class Echo extends Body {
    @JsonProperty("echo")
    public String echo;

    public Echo() {
        super();
    }

    public Echo(String echo) {
        this.echo = echo;
    }
}
