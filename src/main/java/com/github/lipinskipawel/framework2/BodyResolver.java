package com.github.lipinskipawel.framework2;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.AbstractTypeResolver;
import com.fasterxml.jackson.databind.DatabindContext;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.jsontype.TypeIdResolver;

import java.io.IOException;
import java.util.Map;

import static com.github.lipinskipawel.framework2.Register.subTypes;

public final class BodyResolver extends AbstractTypeResolver implements TypeIdResolver {

    @Override
    public void init(JavaType baseType) {
    }

    @Override
    public String idFromValue(Object value) {
        return subTypes
                .entrySet()
                .stream()
                .filter(it -> it.getValue().getRawClass().isAssignableFrom(value.getClass()))
                .map(Map.Entry::getKey)
                .findFirst()
                .orElseThrow();
    }

    @Override
    public String idFromValueAndType(Object value, Class<?> suggestedType) {
        throw new RuntimeException("idFromValueAndType");
    }

    @Override
    public String idFromBaseType() {
        throw new RuntimeException("idFromBaseType");
    }

    @Override
    public JavaType typeFromId(DatabindContext context, String id) throws IOException {
        return subTypes.get(id);
    }

    @Override
    public String getDescForKnownTypeIds() {
        throw new RuntimeException("getDescForKnownTypeIds");
    }

    @Override
    public JsonTypeInfo.Id getMechanism() {
        throw new RuntimeException("getMechanism");
    }
}
