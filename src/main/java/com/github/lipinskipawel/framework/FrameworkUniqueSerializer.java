package com.github.lipinskipawel.framework;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import java.io.IOException;
import java.util.UUID;

public final class FrameworkUniqueSerializer extends StdSerializer<Unique> {

    public FrameworkUniqueSerializer() {
        super(Unique.class);
    }

    @Override
    public void serialize(Unique value, JsonGenerator gen, SerializerProvider provider) {
        value.id()
                .map(UUID::toString)
                .ifPresent(it -> writeId(gen, it));
    }

    private void writeId(JsonGenerator gen, String uuid) {
        try {
            gen.writeStringField("id", uuid);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
