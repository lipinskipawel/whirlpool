package com.github.lipinskipawel.framework2.protocol;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.github.lipinskipawel.framework2.Body;

import java.util.List;
import java.util.Map;

public final class Topology extends Body {
    @JsonProperty("topology")
    public Map<String, List<String>> topology;

    public Topology() {
    }

    public Topology(Map<String, List<String>> topology) {
        this.topology = topology;
    }
}
