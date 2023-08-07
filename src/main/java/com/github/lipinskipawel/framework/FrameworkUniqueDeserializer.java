package com.github.lipinskipawel.framework;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.github.lipinskipawel.framework.BuiltInBodies.Unique;

import java.io.IOException;
import java.util.UUID;

import static java.util.Optional.ofNullable;

public final class FrameworkUniqueDeserializer extends StdDeserializer<Unique> {

    public FrameworkUniqueDeserializer() {
        super(Unique.class);
    }

    @Override
    public Unique deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        final JsonNode tree = p.getCodec().readTree(p);
        return new Unique(ofNullable(tree.get("id"))
                .map(JsonNode::asText)
                .map(UUID::fromString));
    }
}
