package com.github.lipinskipawel.framework;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import java.io.IOException;

import static java.util.Comparator.comparingInt;

public final class FrameworkBroadcastSerializer extends StdSerializer<Broadcast> {

    public FrameworkBroadcastSerializer() {
        super(Broadcast.class);
    }

    @Override
    public void serialize(Broadcast value, JsonGenerator gen, SerializerProvider provider) throws IOException {
        value.message().ifPresent(it -> {
            try {
                gen.writeNumberField("message", it);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        if (!value.messagesFromOtherNodes().isEmpty()) {
            gen.writeFieldName("messages_from_other_node");
            gen.writeArray(value.messagesFromOtherNodes().stream().mapToInt(it -> it).toArray(), 0, value.messagesFromOtherNodes().size());
        }

        if (!value.visitedNodes().isEmpty()) {
            gen.writeFieldName("visited_nodes");
            gen.writeArray(value.visitedNodes().toArray(new String[0]), 0, value.visitedNodes().size());
        }

        if (!value.messages().isEmpty()) {
            gen.writeFieldName("messages");
            gen.writeArray(value.messages().stream().mapToInt(it -> it).toArray(), 0, value.messages().size());
        }

        if (!value.topology().isEmpty()) {
            gen.writeObjectFieldStart("topology");
            value.topology().entrySet()
                    .stream()
                    .sorted(comparingInt(a -> a.getValue().size()))
                    .forEach(entry -> {
                        try {
                            gen.writeFieldName(entry.getKey());
                            gen.writeArray(entry.getValue().toArray(new String[0]), 0, entry.getValue().size());
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    });
            gen.writeEndObject();
        }
    }
}
