package com.github.lipinskipawel.protocol;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.function.Consumer;

import static com.github.lipinskipawel.protocol.EchoBody.Builder.echoBodyBuilder;
import static com.github.lipinskipawel.protocol.InitBody.Builder.initBodyBuilder;
import static com.github.lipinskipawel.protocol.UniqueBody.Builder.uniqueBodyBuilder;

public record Message<T>(
        @JsonProperty("src") String src,
        @JsonProperty("dest") String dst,
        @JsonProperty("body") T body
) {

    public static Message<InitBody> messageWithInitBody(String src, String dst, Consumer<InitBody.Builder> bodyBuilder) {
        final var builder = initBodyBuilder();
        bodyBuilder.accept(builder);
        var body = builder.build();
        return new Message<>(src, dst, body);
    }

    public static Message<EchoBody> messageWithEchoBody(String src, String dst, Consumer<EchoBody.Builder> bodyBuilder) {
        final var builder = echoBodyBuilder();
        bodyBuilder.accept(builder);
        var body = builder.build();
        return new Message<>(src, dst, body);
    }

    public static Message<UniqueBody> messageWithUniqueBody(String src, String dst, Consumer<UniqueBody.Builder> bodyBuilder) {
        final var builder = uniqueBodyBuilder();
        bodyBuilder.accept(builder);
        var body = builder.build();
        return new Message<>(src, dst, body);
    }
}
