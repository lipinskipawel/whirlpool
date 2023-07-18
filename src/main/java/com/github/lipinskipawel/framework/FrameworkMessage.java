package com.github.lipinskipawel.framework;

import java.util.Objects;
import java.util.Optional;

import static com.github.lipinskipawel.framework.FrameworkMessage.Builder.frameworkMessage;
import static java.util.Optional.empty;
import static java.util.Optional.of;

public final class FrameworkMessage<T> {
    private final int id;
    private final String src;
    private final String dst;
    private final T body;

    private final String type;
    private final Optional<Integer> msgId;
    private final Optional<Integer> inReplyTo;

    private FrameworkMessage(Builder<T> builder) {
        this.id = builder.id;
        this.src = builder.src;
        this.dst = builder.dst;
        this.body = builder.body;

        this.type = builder.type;
        this.msgId = builder.msgId;
        this.inReplyTo = builder.inReplyTo;
    }

    public FrameworkMessage<T> reply(FrameworkMessage<T> replyMessage) {
        final var builder = replyMessage.copy()
                .withSrc(dst)
                .withDst(src)
                .withType(convertType(this))
                .withInReplyTo(msgId.or(() -> replyMessage.msgId).orElse(1));
        return builder.build();
    }

    private String convertType(FrameworkMessage<T> message) {
        return switch (message.getType()) {
            case "init" -> "init_ok";
            case "echo" -> "echo_ok";
            case "generate" -> "generate_ok";
            default -> throw new RuntimeException("Not supported type %s" + message.getType());
        };
    }

    FrameworkMessage.Builder<?> copy() {
        final var builder = frameworkMessage()
                .withId(id)
                .withSrc(src)
                .withDst(dst)
                .withBody(body)
                .withType(type);
        msgId.ifPresent(builder::withMsgId);
        inReplyTo.ifPresent(builder::withInReplyTo);
        return builder;
    }

    int getId() {
        return id;
    }

    String getSrc() {
        return src;
    }

    String getDst() {
        return dst;
    }

    T getBody() {
        return body;
    }

    String getType() {
        return type;
    }

    Optional<Integer> getMsgId() {
        return msgId;
    }

    Optional<Integer> getInReplyTo() {
        return inReplyTo;
    }

    @Override
    public String toString() {
        return "FrameworkMessage{" +
                "id=" + id +
                ", src='" + src + '\'' +
                ", dst='" + dst + '\'' +
                ", body=" + body +
                ", type='" + type + '\'' +
                ", msgId=" + msgId +
                ", inReplyTo=" + inReplyTo +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        final var that = (FrameworkMessage<?>) o;
        return id == that.id &&
                Objects.equals(src, that.src) &&
                Objects.equals(dst, that.dst) &&
                Objects.equals(body, that.body) &&
                Objects.equals(type, that.type) &&
                Objects.equals(msgId, that.msgId) &&
                Objects.equals(inReplyTo, that.inReplyTo);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, src, dst, body, type, msgId, inReplyTo);
    }

    public static class Builder<T> {
        private int id;
        private String src;
        private String dst;
        private T body;

        private String type;
        private Optional<Integer> msgId = empty();
        private Optional<Integer> inReplyTo = empty();

        private Builder() {
        }

        public static <T> Builder<T> frameworkMessage() {
            return new Builder<>();
        }

        Builder<T> withId(int id) {
            this.id = id;
            return this;
        }

        Builder<T> withSrc(String src) {
            this.src = src;
            return this;
        }

        Builder<T> withDst(String dst) {
            this.dst = dst;
            return this;
        }

        public Builder<T> withBody(T body) {
            this.body = body;
            return this;
        }

        Builder<T> withType(String type) {
            this.type = type;
            return this;
        }

        Builder<T> withMsgId(int msgId) {
            this.msgId = of(msgId);
            return this;
        }

        Builder<T> withInReplyTo(int inReplyTo) {
            this.inReplyTo = of(inReplyTo);
            return this;
        }

        @SuppressWarnings("unchecked")
        public <T> FrameworkMessage<T> build() {
            return (FrameworkMessage<T>) new FrameworkMessage<>(this);
        }
    }
}
