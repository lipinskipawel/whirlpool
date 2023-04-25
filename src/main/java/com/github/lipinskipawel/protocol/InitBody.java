package com.github.lipinskipawel.protocol;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Optional;

import static java.util.Optional.empty;
import static java.util.Optional.of;

public record InitBody(
        @JsonProperty("type") String type,
        @JsonProperty("msg_id") Optional<Integer> msgId,
        @JsonProperty("in_reply_to") Optional<Integer> inReplyTo,
        @JsonProperty("node_id") Optional<String> nodeId,
        @JsonProperty("node_ids") Optional<List<String>> nodeIds
) {

    public static final class Builder {
        private String type;
        private Optional<Integer> msgId = empty();
        private Optional<Integer> inReplyTo = empty();
        private Optional<String> nodeId = empty();
        private Optional<List<String>> nodeIds = empty();

        private Builder() {
        }

        public static Builder initBodyBuilder() {
            return new InitBody.Builder();
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

        public Builder withNodeId(String nodeId) {
            this.nodeId = of(nodeId);
            return this;
        }

        public Builder withNodeIds(List<String> nodesIds) {
            this.nodeIds = of(List.copyOf(nodesIds));
            return this;
        }

        InitBody build() {
            return new InitBody(type, msgId, inReplyTo, nodeId, nodeIds);
        }
    }
}
