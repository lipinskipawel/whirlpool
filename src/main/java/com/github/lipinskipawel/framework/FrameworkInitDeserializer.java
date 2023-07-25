package com.github.lipinskipawel.framework;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.github.lipinskipawel.framework.BuiltInBodies.Init;

import java.io.IOException;
import java.util.stream.Stream;

import static java.util.List.of;
import static java.util.Optional.ofNullable;
import static java.util.Spliterators.spliteratorUnknownSize;
import static java.util.stream.StreamSupport.stream;

public final class FrameworkInitDeserializer extends StdDeserializer<Init> {

    public FrameworkInitDeserializer() {
        super(Init.class);
    }

    @Override
    public Init deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        final JsonNode tree = p.getCodec().readTree(p);

        final var nodeId = tree.get("node_id").asText();
        final var nodeIds = ofNullable(tree.get("node_ids"))
                .map(JsonNode::elements)
                .map(it -> spliteratorUnknownSize(it, 0))
                .map(it -> stream(it, false))
                .map(it -> it.map(JsonNode::asText))
                .map(Stream::toList)
                .orElse(of());
        return new Init(nodeId, nodeIds);
    }
}
