package com.github.lipinskipawel.framework1;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;

import java.io.IOException;

import static com.github.lipinskipawel.framework1.Server.POSSIBLE_TYPES;

final class EventDeserializer extends JsonDeserializer<Event<?>> {

    public EventDeserializer() {
    }

    @Override
    public Event<?> deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        final JsonNode tree = p.getCodec().readTree(p);

        final var id = tree.get("id").asInt();
        final var src = tree.get("src").asText();
        final var dst = tree.get("dest").asText();
        final var bodyNode = tree.get("body");
        final var intoType = POSSIBLE_TYPES.get(bodyNode.get("type").asText());
        final var object = bodyNode.traverse(p.getCodec()).readValueAs(intoType);

        final var event = new Event<>(object);
        event.id = id;
        event.src = src;
        event.dst = dst;
        return event;
    }
}
