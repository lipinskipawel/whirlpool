package com.github.lipinskipawel.framework1;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Objects;

public final class Event<T> {
    @JsonProperty("id")
    public int id;
    @JsonProperty("src")
    public String src;
    @JsonProperty("dest")
    public String dst;
    @JsonProperty("body")
    public T body;

    public Event() {
    }

    public Event(T body) {
        this.body = body;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Event<?> event = (Event<?>) o;
        return id == event.id && Objects.equals(src, event.src) && Objects.equals(dst, event.dst) && Objects.equals(body, event.body);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, src, dst, body);
    }
}
