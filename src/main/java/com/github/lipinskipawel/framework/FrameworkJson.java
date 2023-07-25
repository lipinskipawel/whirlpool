package com.github.lipinskipawel.framework;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.github.lipinskipawel.framework.BuiltInBodies.Init;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_ABSENT;
import static com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES;

public final class FrameworkJson {

    private static final SimpleModule simpleModule = new SimpleModule()
            .addDeserializer(FrameworkMessage.class, new FrameworkMessageDeserializer())
            .addSerializer(FrameworkMessage.class, new FrameworkMessageSerializer())
            .addDeserializer(Init.class, new FrameworkInitDeserializer())
            .addSerializer(Init.class, new FrameworkInitSerializer());

    private static final ObjectMapper mapper = new ObjectMapper()
            .registerModule(new Jdk8Module())
            .registerModule(simpleModule)
            .configure(FAIL_ON_UNKNOWN_PROPERTIES, true)
            .setSerializationInclusion(NON_ABSENT);

    private FrameworkJson() {
    }

    public static ObjectMapper mapper() {
        return mapper;
    }

    public static <T> ObjectMapper configureObjectMapper(Class<T> type, StdDeserializer<T> deserializer, StdSerializer<T> serializer) {
        return mapper.copy()
                .registerModule(simpleModule
                        .addDeserializer(type, deserializer)
                        .addSerializer(type, serializer));
    }

    public static <T> String toJson(T object) {
        try {
            return mapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public static <T> T toObject(String json, TypeReference<T> typeReference) {
        try {
            return mapper.readValue(json, typeReference);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public static <T extends FrameworkMessage<?>> T toObject(String json, JavaType javaType) {
        try {
            return mapper.readValue(json, javaType);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
