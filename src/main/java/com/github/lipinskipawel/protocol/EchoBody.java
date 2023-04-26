package com.github.lipinskipawel.protocol;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Optional;

import static java.util.Optional.empty;
import static java.util.Optional.of;

public record EchoBody(
        @JsonProperty("type") String type,
        @JsonProperty("msg_id") int msgId,
        @JsonProperty("in_reply_to") Optional<Integer> inReplyTo,
        @JsonProperty("echo") String echo
) {

    public static final class Builder {
        private String type;
        private int msgId;
        private Optional<Integer> inReplyTo = empty();
        private String echo;

        private Builder() {
        }

        public static Builder bodyBuilder() {
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

        public Builder withEcho(String echo) {
            this.echo = echo;
            return this;
        }

        EchoBody build() {
            return new EchoBody(type, msgId, inReplyTo, echo);
        }
    }
}
