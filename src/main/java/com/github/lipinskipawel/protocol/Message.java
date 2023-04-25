package com.github.lipinskipawel.protocol;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.function.Consumer;

import static com.github.lipinskipawel.protocol.Body.Builder.bodyBuilder;
import static com.github.lipinskipawel.protocol.InitBody.Builder.initBodyBuilder;

public record Message<T>(
        @JsonProperty("src") String src,
        @JsonProperty("dest") String dst,
        @JsonProperty("body") T body
) {

    public static Message<Body> messageWithBody(String src, String dst, Consumer<Body.Builder> bodyBuilder) {
        final var builder = bodyBuilder();
        bodyBuilder.accept(builder);
        var body = builder.build();
        return new Message<>(src, dst, body);
    }

    public static Message<InitBody> messageWithInitBody(String src, String dst, Consumer<InitBody.Builder> bodyBuilder) {
        final var builder = initBodyBuilder();
        bodyBuilder.accept(builder);
        var body = builder.build();
        return new Message<>(src, dst, body);
    }
}
