package com.github.lipinskipawel.framework2.protocol;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.github.lipinskipawel.framework2.Body;

import java.util.List;

public final class Init extends Body {
    @JsonProperty("node_id")
    public String nodeId;
    @JsonProperty("node_ids")
    public List<String> nodeIds;
}
