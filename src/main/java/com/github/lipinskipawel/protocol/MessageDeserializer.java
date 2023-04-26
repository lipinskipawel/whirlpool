package com.github.lipinskipawel.protocol;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

import java.io.IOException;
import java.util.NoSuchElementException;
import java.util.stream.Stream;

import static java.util.Optional.ofNullable;
import static java.util.Spliterators.spliteratorUnknownSize;
import static java.util.stream.StreamSupport.stream;

public class MessageDeserializer extends StdDeserializer<Message<?>> {

    public MessageDeserializer() {
        super(Message.class);
    }

    @Override
    public Message<?> deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        final JsonNode tree = p.getCodec().readTree(p);

        final var src = tree.get("src").asText();
        final var dst = tree.get("dest").asText();
        final var body = deserializeBody(tree.get("body"));

        return new Message<>(src, dst, body);
    }

    private Object deserializeBody(JsonNode bodyNode) {
        final var typeOfBody = bodyNode.get("type").asText();
        return switch (typeOfBody) {
            case "init" -> initBody(bodyNode);
            case "echo" -> echoBody(bodyNode);
            default -> throw new NoSuchElementException("Unknown type of message body {%s}".formatted(typeOfBody));
        };
    }

    private Object initBody(JsonNode bodyNode) {
        final var type = bodyNode.get("type").asText();
        final var msgId = ofNullable(bodyNode.get("msg_id")).map(JsonNode::asInt);
        final var inReplyTo = ofNullable(bodyNode.get("in_reply_to")).map(JsonNode::asInt);
        final var nodeId = ofNullable(bodyNode.get("node_id")).map(JsonNode::asText);
        final var elements = ofNullable(bodyNode.get("node_ids"))
                .map(JsonNode::elements)
                .map(it -> spliteratorUnknownSize(it, 0))
                .map(it -> stream(it, false))
                .map(it -> it.map(JsonNode::asText))
                .map(Stream::toList);
        return new InitBody(type, msgId, inReplyTo, nodeId, elements);
    }

    private Object echoBody(JsonNode bodyNode) {
        final var type = bodyNode.get("type").asText();
        final var msgId = bodyNode.get("msg_id").asInt();
        final var inReplyTo = ofNullable(bodyNode.get("in_reply_to")).map(JsonNode::asInt);
        final var echo = bodyNode.get("echo").asText();
        return new EchoBody(type, msgId, inReplyTo, echo);
    }
}
