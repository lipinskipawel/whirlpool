package com.github.lipinskipawel.framework2;

import com.fasterxml.jackson.annotation.JsonProperty;

public final class Event {
    @JsonProperty("id")
    public int id;
    @JsonProperty("src")
    public String src;
    @JsonProperty("dest")
    public String dst;
    public Body body;

    public Event() {
    }

    public Event(Body body) {
        this.body = body;
    }
}
