package com.github.lipinskipawel.protocol;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Optional;
import java.util.UUID;

import static java.util.Optional.empty;
import static java.util.Optional.of;

public record UniqueBody(
        @JsonProperty("type") String type,
        @JsonProperty("msg_id") int msgId,
        @JsonProperty("in_reply_to") Optional<Integer> inReplyTo,
        @JsonProperty("id") Optional<UUID> id
) {

    public static final class Builder {
        private String type;
        private int msgId;
        private Optional<Integer> inReplyTo = empty();
        private Optional<UUID> id = empty();

        private Builder() {
        }

        public static Builder uniqueBodyBuilder() {
            return new Builder();
        }

        public Builder withType(String type) {
            this.type = type;
            return this;
        }

        public Builder withMsgId(int msgId) {
            this.msgId = msgId;
            return this;
        }

        public Builder withInReplyTo(int inReplyTo) {
            this.inReplyTo = of(inReplyTo);
            return this;
        }

        public Builder withId(UUID id) {
            this.id = of(id);
            return this;
        }

        UniqueBody build() {
            return new UniqueBody(type, msgId, inReplyTo, id);
        }
    }
}
