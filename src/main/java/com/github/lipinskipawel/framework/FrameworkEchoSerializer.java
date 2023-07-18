package com.github.lipinskipawel.framework;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import com.github.lipinskipawel.framework.FrameworkEchoBody;

import java.io.IOException;

public class FrameworkEchoSerializer extends StdSerializer<FrameworkEchoBody> {

    public FrameworkEchoSerializer() {
        super(FrameworkEchoBody.class);
    }

    @Override
    public void serialize(FrameworkEchoBody value, JsonGenerator gen, SerializerProvider provider) throws IOException {
        gen.writeStringField("echo", value.echo());
    }
}
