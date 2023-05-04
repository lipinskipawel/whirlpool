package com.github.lipinskipawel.protocol;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Optional;

import static java.util.Optional.empty;
import static java.util.Optional.of;

public record BroadcastBody(
        @JsonProperty("type") String type,
        @JsonProperty("msg_id") Optional<Integer> msgId,
        @JsonProperty("in_reply_to") Optional<Integer> inReplyTo,
        @JsonProperty("message") Optional<Integer> message,
        @JsonProperty("messages_from_other_node") Optional<List<Integer>> messagesFromOtherNode
) {

    public static final class Builder {
        private String type;
        private Optional<Integer> msgId = empty();
        private Optional<Integer> inReplyTo = empty();
        private Optional<List<Integer>> messagesFromOtherNode = empty();

        private Builder() {
        }

        public static Builder broadcastBodyBuilder() {
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

        public Builder withMessagesFromOtherNode(List<Integer> messages) {
            this.messagesFromOtherNode = of(messages);
            return this;
        }

        BroadcastBody build() {
            return new BroadcastBody(type, msgId, inReplyTo, empty(), messagesFromOtherNode);
        }
    }
}
