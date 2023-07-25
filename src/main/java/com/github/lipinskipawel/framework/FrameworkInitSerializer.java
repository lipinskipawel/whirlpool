package com.github.lipinskipawel.framework;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import com.github.lipinskipawel.framework.BuiltInBodies.Init;

public final class FrameworkInitSerializer extends StdSerializer<Init> {

    public FrameworkInitSerializer() {
        super(Init.class);
    }

    @Override
    public void serialize(Init value, JsonGenerator gen, SerializerProvider provider) {
    }
}
