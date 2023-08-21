package com.github.lipinskipawel.base;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Objects;

public final class EchoOk extends EventType {
    @JsonProperty("echo")
    public String echo;

    public EchoOk() {
        super("echo_ok");
    }

    public EchoOk(String echo) {
        super("echo_ok");
        this.echo = echo;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        EchoOk echoOk = (EchoOk) o;
        return Objects.equals(echo, echoOk.echo);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), echo);
    }
}
