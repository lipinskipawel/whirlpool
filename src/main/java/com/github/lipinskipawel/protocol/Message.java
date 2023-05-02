package com.github.lipinskipawel.protocol;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.function.Consumer;

import static com.github.lipinskipawel.protocol.BroadcastBody.Builder.broadcastBodyBuilder;
import static com.github.lipinskipawel.protocol.EchoBody.Builder.echoBodyBuilder;
import static com.github.lipinskipawel.protocol.InitBody.Builder.initBodyBuilder;
import static com.github.lipinskipawel.protocol.ReadBody.Builder.readBodyBuilder;
import static com.github.lipinskipawel.protocol.TopologyBody.Builder.topologyBodyBuilder;
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

    public static Message<BroadcastBody> messageWithBroadcastBody(String src, String dst, Consumer<BroadcastBody.Builder> bodyBuilder) {
        final var builder = broadcastBodyBuilder();
        bodyBuilder.accept(builder);
        var body = builder.build();
        return new Message<>(src, dst, body);
    }

    public static Message<ReadBody> messageWithReadBody(String src, String dst, Consumer<ReadBody.Builder> bodyBuilder) {
        final var builder = readBodyBuilder();
        bodyBuilder.accept(builder);
        var body = builder.build();
        return new Message<>(src, dst, body);
    }

    public static Message<TopologyBody> messageWithTopologyBody(String src, String dst, Consumer<TopologyBody.Builder> bodyBuilder) {
        final var builder = topologyBodyBuilder();
        bodyBuilder.accept(builder);
        var body = builder.build();
        return new Message<>(src, dst, body);
    }
}
