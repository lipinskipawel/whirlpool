package com.github.lipinskipawel.protocol;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;

@JsonDeserialize(builder = Body.Builder.class)
public record Body(
        @JsonProperty("type") String type,
        @JsonProperty("msg_id") int msgId,
        @JsonProperty("in_reply_to") int inReplyTo,
        @JsonProperty("echo") String echo
) {

    private Body(Builder builder) {
        this(builder.type, builder.msgId, builder.inReplyTo, builder.echo);
    }

    @JsonPOJOBuilder(withPrefix = "with")
    public static final class Builder {

        private String type;
        private int msgId;
        private int inReplyTo;
        private String echo;

        @JsonProperty("type")
        public Builder withType(String type) {
            this.type = type;
            return this;
        }

        @JsonProperty("msg_id")
        public Builder withMsgId(int msgId) {
            this.msgId = msgId;
            return this;
        }

        @JsonProperty("in_reply_to")
        public Builder withInReplyTo(int inReplyTo) {
            this.inReplyTo = inReplyTo;
            return this;
        }

        @JsonProperty("echo")
        public Builder withEcho(String echo) {
            this.echo = echo;
            return this;
        }

        public Body build() {
            if (this.echo == null) {
                this.echo = "";
            }
            return new Body(this);
        }
    }
}
