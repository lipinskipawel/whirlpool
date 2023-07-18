package com.github.lipinskipawel.framework;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import java.io.IOException;
import java.util.Optional;

public class FrameworkMessageSerializer extends StdSerializer<FrameworkMessage> {

    public FrameworkMessageSerializer() {
        super(FrameworkMessage.class, false);
    }

    @Override
    public void serialize(FrameworkMessage value, JsonGenerator gen, SerializerProvider provider) throws IOException {
        final var id = value.getId();
        final var src = value.getSrc();
        final var dst = value.getDst();

        final Object body = value.getBody();
        final var type = value.getType();
        @SuppressWarnings("unchecked") final Optional<Integer> msgId = value.getMsgId();
        @SuppressWarnings("unchecked") final Optional<Integer> inReplyTo = value.getInReplyTo();

        gen.writeStartObject();
        gen.writeNumberField("id", id);
        gen.writeStringField("src", src);
        gen.writeStringField("dest", dst);

        gen.writeObjectFieldStart("body");
        gen.writeStringField("type", type);
        msgId.ifPresent(it -> wrapWithCatch(gen, it, "msg_id"));
        inReplyTo.ifPresent(it -> wrapWithCatch(gen, it, "in_reply_to"));
        gen.writeObject(body);
        gen.writeEndObject();
        gen.writeEndObject();
    }

    private void wrapWithCatch(JsonGenerator gen, Integer msgId, String fieldName) {
        try {
            gen.writeNumberField(fieldName, msgId);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
