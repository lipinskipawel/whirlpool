package com.github.lipinskipawel.framework;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

import java.io.IOException;

import static com.github.lipinskipawel.protocol.Json.configureObjectMapper;

public class FrameworkEchoDeserializer extends StdDeserializer<FrameworkEchoBody> {

    public FrameworkEchoDeserializer() {
        super(FrameworkEchoBody.class);
    }

    @Override
    public FrameworkEchoBody deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        p.setCodec(configureObjectMapper());
        final JsonNode tree = p.getCodec().readTree(p);

        final var echo = tree.get("echo").asText();
        return new FrameworkEchoBody(echo);
    }
}
