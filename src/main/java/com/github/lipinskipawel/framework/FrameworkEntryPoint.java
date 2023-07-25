package com.github.lipinskipawel.framework;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import com.fasterxml.jackson.databind.type.TypeFactory;

import java.util.Scanner;

import static com.github.lipinskipawel.framework.BuiltInBodies.Init;
import static com.github.lipinskipawel.framework.FrameworkJson.configureObjectMapper;
import static com.github.lipinskipawel.framework.FrameworkJson.toJson;
import static com.github.lipinskipawel.framework.FrameworkJson.toObject;
import static com.github.lipinskipawel.framework.FrameworkMessage.Builder.frameworkMessage;

public final class FrameworkEntryPoint<T> {
    private final RequestHandler<FrameworkMessage<T>> handler;
    private final JavaType type;

    @SuppressWarnings("unchecked")
    private FrameworkEntryPoint(
            RequestHandler<FrameworkMessage<T>> handler,
            StdDeserializer<T> deserializer,
            StdSerializer<T> serializer) {
        this.handler = handler;
        this.type = TypeFactory.defaultInstance().constructFromCanonical(deserializer.handledType().getCanonicalName());
        configureObjectMapper((Class<T>) deserializer.handledType(), deserializer, serializer);
    }

    public static <T> FrameworkEntryPoint<T> register(
            RequestHandler<FrameworkMessage<T>> handler,
            StdDeserializer<T> deserializer,
            StdSerializer<T> serializer) {
        return new FrameworkEntryPoint<>(handler, deserializer, serializer);
    }

    public void start() {
        try (var scanner = new Scanner(System.in)) {
            final var initRequest = scanner.nextLine();
            final var initMessage = toObject(initRequest, new TypeReference<FrameworkMessage<Init>>() {
            });
            final var initOk = initMessage.reply(frameworkMessage()
                    .withBody(new Init())
                    .build());
            System.out.println(toJson(initOk));

            while (scanner.hasNextLine()) {
                final var request = scanner.nextLine();
                final var javaType = TypeFactory.defaultInstance().constructParametricType(FrameworkMessage.class, type);
                final FrameworkMessage<T> message = toObject(request, javaType);
                handler.handle(message);
            }
        }
    }
}
