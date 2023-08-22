package com.github.lipinskipawel.base;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Objects;

public final class ReadOk extends EventType {
    @JsonProperty("messages")
    public List<Integer> messages;

    public ReadOk() {
        super("read_ok");
    }

    public ReadOk(List<Integer> messages) {
        super("read_ok");
        this.messages = messages;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        ReadOk readOk = (ReadOk) o;
        return Objects.equals(messages, readOk.messages);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), messages);
    }
}