package com.github.lipinskipawel.protocol;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static java.util.Optional.empty;
import static java.util.Optional.of;

public record TopologyBody(
        @JsonProperty("type") String type,
        @JsonProperty("msg_id") Optional<Integer> msgId,
        @JsonProperty("in_reply_to") Optional<Integer> inReplyTo,
        @JsonProperty("topology") Optional<Map<String, List<String>>> topology
) {

    public static final class Builder {
        private String type;
        private Optional<Integer> msgId = empty();
        private Optional<Integer> inReplyTo = empty();
        private Optional<Map<String, List<String>>> topology = empty();

        private Builder() {
        }

        public static Builder topologyBodyBuilder() {
            return new Builder();
        }

        public Builder withType(String type) {
            this.type = type;
            return this;
        }

        public Builder withMsgId(int msgId) {
            this.msgId = of(msgId);
            return this;
        }

        public Builder withInReplyTo(int inReplyTo) {
            this.inReplyTo = of(inReplyTo);
            return this;
        }

        public Builder withTopology(Map<String, List<String>> topology) {
            this.topology = of(topology);
            return this;
        }

        TopologyBody build() {
            return new TopologyBody(type, msgId, inReplyTo, topology);
        }
    }
}
