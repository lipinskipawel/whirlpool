package com.github.lipinskipawel.framework;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import java.io.IOException;

public class FrameworkEchoSerializer extends StdSerializer<Echo> {

    public FrameworkEchoSerializer() {
        super(Echo.class);
    }

    @Override
    public void serialize(Echo value, JsonGenerator gen, SerializerProvider provider) throws IOException {
        gen.writeStringField("echo", value.echo());
    }
}
