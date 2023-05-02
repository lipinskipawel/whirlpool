package com.github.lipinskipawel.protocol;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Optional;

import static java.util.Optional.empty;
import static java.util.Optional.of;

public record ReadBody(
        @JsonProperty("type") String type,
        @JsonProperty("msg_id") Optional<Integer> msgId,
        @JsonProperty("in_reply_to") Optional<Integer> inReplyTo,
        @JsonProperty("messages") Optional<List<Integer>> messages
) {

    public static final class Builder {
        private String type;
        private Optional<Integer> msgId = empty();
        private Optional<Integer> inReplyTo = empty();
        private Optional<List<Integer>> messages = empty();

        private Builder() {
        }

        public static Builder readBodyBuilder() {
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

        public Builder withMessages(List<Integer> messages) {
            this.messages = of(messages);
            return this;
        }

        ReadBody build() {
            return new ReadBody(type, msgId, inReplyTo, messages);
        }
    }
}
