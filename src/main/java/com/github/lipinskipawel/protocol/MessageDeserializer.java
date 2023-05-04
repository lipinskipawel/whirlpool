package com.github.lipinskipawel.protocol;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.UUID;
import java.util.stream.Stream;

import static java.util.Optional.of;
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
            case "generate" -> uniqueBody(bodyNode);
            case "broadcast", "internal" -> broadcastBody(bodyNode);
            case "read" -> readBody(bodyNode);
            case "topology" -> topologyBody(bodyNode);
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

    private Object uniqueBody(JsonNode bodyNode) {
        final var type = bodyNode.get("type").asText();
        final var msgId = bodyNode.get("msg_id").asInt();
        final var inReplyTo = ofNullable(bodyNode.get("in_reply_to")).map(JsonNode::asInt);
        final var id = ofNullable(bodyNode.get("id")).map(JsonNode::asText).map(UUID::fromString);
        return new UniqueBody(type, msgId, inReplyTo, id);
    }

    private Object broadcastBody(JsonNode bodyNode) {
        final var type = bodyNode.get("type").asText();
        final var msgId = ofNullable(bodyNode.get("msg_id")).map(JsonNode::asInt);
        final var inReplyTo = ofNullable(bodyNode.get("in_reply_to")).map(JsonNode::asInt);
        final var message = ofNullable(bodyNode.get("message")).map(JsonNode::asInt);
        final var messagesFromOtherNode = ofNullable(bodyNode.get("messages_from_other_node"))
                .map(JsonNode::elements)
                .map(it -> spliteratorUnknownSize(it, 0))
                .map(it -> stream(it, false))
                .map(it -> it.map(JsonNode::asInt))
                .map(Stream::toList);
        return new BroadcastBody(type, msgId, inReplyTo, message, messagesFromOtherNode);
    }

    private Object readBody(JsonNode bodyNode) {
        final var type = bodyNode.get("type").asText();
        final var msgId = ofNullable(bodyNode.get("msg_id")).map(JsonNode::asInt);
        final var inReplyTo = ofNullable(bodyNode.get("in_reply_to")).map(JsonNode::asInt);
        final var messages = ofNullable(bodyNode.get("messages"))
                .map(JsonNode::elements)
                .map(it -> spliteratorUnknownSize(it, 0))
                .map(it -> stream(it, false))
                .map(it -> it.map(JsonNode::asInt))
                .map(Stream::toList);
        return new ReadBody(type, msgId, inReplyTo, messages);
    }

    private Object topologyBody(JsonNode bodyNode) {
        final var type = bodyNode.get("type").asText();
        final var msgId = ofNullable(bodyNode.get("msg_id")).map(JsonNode::asInt);
        final var inReplyTo = ofNullable(bodyNode.get("in_reply_to")).map(JsonNode::asInt);
        final Map<String, List<String>> topology = ofNullable(bodyNode.get("topology"))
                .map(JsonNode::fields)
                .map(it -> spliteratorUnknownSize(it, 0))
                .map(it -> stream(it, false))
                .stream()
                .flatMap(it -> it)
                .map(this::mapValues)
                .collect(HashMap::new, (a, b) -> a.put(b.getKey(), b.getValue()), HashMap::putAll);
        return new TopologyBody(type, msgId, inReplyTo, of(topology));
    }

    private Map.Entry<String, List<String>> mapValues(Map.Entry<String, JsonNode> entry) {
        final var list = ofNullable(entry.getValue())
                .map(JsonNode::elements)
                .map(it -> spliteratorUnknownSize(it, 0))
                .map(it -> stream(it, false))
                .map(it -> it.map(JsonNode::asText))
                .map(Stream::toList)
                .orElse(List.of());
        return Map.entry(entry.getKey(), list);
    }
}
