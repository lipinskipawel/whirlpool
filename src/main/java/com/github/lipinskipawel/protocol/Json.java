package com.github.lipinskipawel.protocol;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_ABSENT;
import static com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES;

public final class Json {
    private static final ObjectMapper mapper = configureObjectMapper();

    private static ObjectMapper configureObjectMapper() {
        return new ObjectMapper()
                .registerModule(new Jdk8Module())
                .registerModule(new SimpleModule()
                        .addDeserializer(Message.class, new MessageDeserializer()))
                .configure(FAIL_ON_UNKNOWN_PROPERTIES, true)
                .setSerializationInclusion(NON_ABSENT);
    }

    public static <T> T toObject(String json, TypeReference<T> typeReference) {
        try {
            return mapper.readValue(json, typeReference);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public static <T> String toJson(T object) {
        try {
            return mapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public static String typeOfJson(String json) {
        try {
            final var tree = mapper.readTree(json);
            return tree.get("body").get("type").asText();
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
