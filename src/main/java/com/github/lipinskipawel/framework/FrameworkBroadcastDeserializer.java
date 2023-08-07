package com.github.lipinskipawel.framework;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static com.github.lipinskipawel.framework.Broadcast.broadcast;
import static java.util.Collections.emptyList;
import static java.util.Collections.emptyMap;
import static java.util.Optional.ofNullable;
import static java.util.Spliterators.spliteratorUnknownSize;
import static java.util.stream.Collectors.toMap;
import static java.util.stream.StreamSupport.stream;

public final class FrameworkBroadcastDeserializer extends StdDeserializer<Broadcast> {

    public FrameworkBroadcastDeserializer() {
        super(Broadcast.class);
    }

    @Override
    public Broadcast deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        final JsonNode tree = p.getCodec().readTree(p);

        final var type = tree.get("type").asText();

        final var message = ofNullable(tree.get("message")).map(JsonNode::asInt);

        final var messagesFromOtherNodes = ofNullable(tree.get("messages_from_other_node"))
                .map(JsonNode::elements)
                .map(it -> spliteratorUnknownSize(it, 0))
                .map(it -> stream(it, false))
                .map(it -> it.map(JsonNode::asInt))
                .map(Stream::toList)
                .orElse(emptyList());

        final var visitedNodes = ofNullable(tree.get("visited_nodes"))
                .map(JsonNode::elements)
                .map(it -> spliteratorUnknownSize(it, 0))
                .map(it -> stream(it, false))
                .map(it -> it.map(JsonNode::asText))
                .map(Stream::toList)
                .orElseGet(Collections::emptyList);

        final var messages = ofNullable(tree.get("messages"))
                .map(JsonNode::elements)
                .map(it -> spliteratorUnknownSize(it, 0))
                .map(it -> stream(it, false))
                .map(it -> it.map(JsonNode::asInt))
                .map(Stream::toList)
                .orElse(emptyList());

        final var topology = ofNullable(tree.get("topology"))
                .map(JsonNode::fields)
                .map(it -> spliteratorUnknownSize(it, 0))
                .map(it -> stream(it, false))
                .map(it -> it.collect(toMap(Map.Entry::getKey, e -> fromJsonNode(e.getValue()))))
                .orElse(emptyMap());


        return broadcast(type)
                .withMessage(message)
                .withMessagesFromOtherNodes(messagesFromOtherNodes)
                .withVisitedNodes(visitedNodes)
                .withMessages(messages)
                .withTopology(topology)
                .build();
    }

    private List<String> fromJsonNode(JsonNode jsonNode) {
        return ofNullable(jsonNode.elements())
                .map(it -> spliteratorUnknownSize(it, 0))
                .map(it -> stream(it, false))
                .map(it -> it.map(JsonNode::asText))
                .map(Stream::toList)
                .orElse(emptyList());
    }
}
