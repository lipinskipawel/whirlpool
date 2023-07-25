package com.github.lipinskipawel.framework;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

import java.io.IOException;

public class FrameworkEchoDeserializer extends StdDeserializer<Echo> {

    public FrameworkEchoDeserializer() {
        super(Echo.class);
    }

    @Override
    public Echo deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        final JsonNode tree = p.getCodec().readTree(p);
        return new Echo(tree.get("echo").asText());
    }
}
