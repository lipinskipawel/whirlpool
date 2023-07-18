package com.github.lipinskipawel.framework;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.ContextualDeserializer;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

import java.io.IOException;

import static com.github.lipinskipawel.framework.FrameworkMessage.Builder.frameworkMessage;
import static java.util.Optional.ofNullable;

public class FrameworkMessageDeserializer extends StdDeserializer<FrameworkMessage<?>> implements ContextualDeserializer {
    private JavaType javaType;
    private BeanProperty beanProperty;

    public FrameworkMessageDeserializer() {
        super(FrameworkMessage.class);
    }

    FrameworkMessageDeserializer(JavaType javaType, BeanProperty beanProperty) {
        super(FrameworkMessage.class);
        this.javaType = javaType;
        this.beanProperty = beanProperty;
    }

    @Override
    public JsonDeserializer<?> createContextual(DeserializationContext ctxt, BeanProperty property) {
        return new FrameworkMessageDeserializer(ctxt.getContextualType(), property);
    }

    @Override
    public FrameworkMessage<?> deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        final JsonNode tree = p.getCodec().readTree(p);

        final var id = tree.get("id").asInt();
        final var src = tree.get("src").asText();
        final var dst = tree.get("dest").asText();
        final var bodyNode = tree.get("body");
        final var contextualValueDeserializer = ctxt.findContextualValueDeserializer(javaType.containedType(0), beanProperty);
        final var object = contextualValueDeserializer.deserialize(bodyNode.traverse(), ctxt);

        final var type = bodyNode.get("type").asText();
        final var msgId = ofNullable(bodyNode.get("msg_id")).map(JsonNode::asInt);
        final var inReplyTo = ofNullable(bodyNode.get("in_reply_to")).map(JsonNode::asInt);

        final var builder = frameworkMessage()
                .withId(id)
                .withSrc(src)
                .withDst(dst)
                .withBody(object)
                .withType(type);
        msgId.ifPresent(builder::withMsgId);
        inReplyTo.ifPresent(builder::withInReplyTo);
        return builder.build();
    }
}
