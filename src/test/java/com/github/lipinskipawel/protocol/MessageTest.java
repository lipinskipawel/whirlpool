package com.github.lipinskipawel.protocol;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_ABSENT;
import static com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES;
import static com.github.lipinskipawel.protocol.Message.messageWithEchoBody;
import static com.github.lipinskipawel.protocol.Message.messageWithInitBody;
import static com.github.lipinskipawel.protocol.Message.messageWithUniqueBody;
import static java.util.UUID.randomUUID;

class MessageTest implements WithAssertions {

    private final ObjectMapper mapper = configureObjectMapper();

    private ObjectMapper configureObjectMapper() {
        return new ObjectMapper()
                .registerModule(new Jdk8Module())
                .registerModule(new SimpleModule()
                        .addDeserializer(Message.class, new MessageDeserializer()))
                .configure(FAIL_ON_UNKNOWN_PROPERTIES, true)
                .setSerializationInclusion(NON_ABSENT);
    }

    @Nested
    class InitEchoBodyTest {
        @Nested
        class SerializationTest {
            @Test
            void should_serialize_complete_message_with_complete_init_body() throws JsonProcessingException {
                var initMessage = messageWithInitBody("1", "2", body -> body
                        .withType("init")
                        .withMsgId(14)
                        .withInReplyTo(12)
                        .withNodeId("n1")
                        .withNodeIds(List.of("n1", "n2", "n3"))
                );

                var json = mapper.writeValueAsString(initMessage);

                assertThat(json).isEqualTo("""
                        {
                            "src":"1",
                            "dest":"2",
                            "body":{
                                "type":"init",
                                "msg_id":14,
                                "in_reply_to":12,
                                "node_id":"n1",
                                "node_ids":["n1","n2","n3"]
                            }
                        }""".replaceAll("\n", "").replaceAll(" ", ""));
            }

            @Test
            void should_serialize_complete_message_with_not_complete_init_body() throws JsonProcessingException {
                var initMessage = messageWithInitBody("1", "2", body -> body
                        .withType("init")
                        .withMsgId(12)
                        .withNodeId("n1")
                        .withNodeIds(List.of("n1", "n2", "n3"))
                );

                var json = mapper.writeValueAsString(initMessage);

                assertThat(json).isEqualTo("""
                        {
                            "src":"1",
                            "dest":"2",
                            "body":{
                                "type":"init",
                                "msg_id":12,
                                "node_id":"n1",
                                "node_ids":["n1","n2","n3"]
                            }
                        }""".replaceAll("\n", "").replaceAll(" ", ""));
            }
        }

        @Nested
        class DeserializationTest {
            @Test
            void should_deserialize_complete_message_with_complete_init_body() throws JsonProcessingException {
                var jsonMsg = """
                        {
                            "src":"1",
                            "dest":"3",
                            "body": {
                                "type":"init",
                                "msg_id":14,
                                "in_reply_to":133,
                                "node_id":"n1",
                                "node_ids":["n1","n2","n3"]
                            }
                        }""";

                var message = mapper.readValue(jsonMsg, mapper.getTypeFactory().constructParametricType(Message.class, InitBody.class));

                assertThat(message).isEqualTo(messageWithInitBody("1", "3", body -> body
                        .withType("init")
                        .withMsgId(14)
                        .withInReplyTo(133)
                        .withNodeId("n1")
                        .withNodeIds(List.of("n1", "n2", "n3"))
                ));
            }
        }

        @Test
        void should_not_deserialize_message_with_required_type_in_init_body() {
            var jsonMsg = """
                    {
                        "src":"1",
                        "dest":"3",
                        "body": {
                            "msg_id":14,
                            "in_reply_to":133,
                            "node_id":"n1",
                            "node_ids":["n1","n2","n3"]
                        }
                    }""";

            assertThatThrownBy(() -> mapper.readValue(jsonMsg, mapper.getTypeFactory().constructParametricType(Message.class, InitBody.class)))
                    .isInstanceOf(NullPointerException.class)
                    .hasMessage("Cannot invoke \"com.fasterxml.jackson.databind.JsonNode.asText()\" because the return value of \"com.fasterxml.jackson.databind.JsonNode.get(String)\" is null");
        }

        @Test
        void should_deserialize_complete_message_with_not_complete_init_body() throws JsonProcessingException {
            var jsonMsg = """
                    {
                        "src":"1",
                        "dest":"3",
                        "body": {
                            "type":"init",
                            "msg_id":14,
                            "node_id":"n1",
                            "node_ids":["n1","n2","n3"]
                        }
                    }""";

            var message = mapper.readValue(jsonMsg, mapper.getTypeFactory().constructParametricType(Message.class, InitBody.class));

            assertThat(message).isEqualTo(messageWithInitBody("1", "3", body -> body
                    .withType("init")
                    .withMsgId(14)
                    .withNodeId("n1")
                    .withNodeIds(List.of("n1", "n2", "n3"))
            ));
        }
    }

    @Nested
    class EchoBodyTest {
        @Nested
        class SerializationTest {
            @Test
            void should_serialize_complete_message_with_complete_body() throws JsonProcessingException {
                var echoMessage = messageWithEchoBody("1", "2", body -> body
                        .withType("echo")
                        .withMsgId(12)
                        .withInReplyTo(19)
                        .withEcho("echo")
                );

                var json = mapper.writeValueAsString(echoMessage);

                assertThat(json).isEqualTo("""
                        {
                            "src":"1",
                            "dest":"2",
                            "body":{
                                "type":"echo",
                                "msg_id":12,
                                "in_reply_to":19,
                                "echo":"echo"
                            }
                        }""".replaceAll("\n", "").replaceAll(" ", ""));
            }

            @Test
            void should_serialize_complete_message_with_not_complete_body() throws JsonProcessingException {
                var echoMessage = messageWithEchoBody("1", "2", body -> body
                        .withType("echo")
                        .withMsgId(12)
                        .withEcho("Repeat")
                );

                var json = mapper.writeValueAsString(echoMessage);

                assertThat(json).isEqualTo("""
                        {
                            "src":"1",
                            "dest":"2",
                            "body":{
                                "type":"echo",
                                "msg_id":12,
                                "echo":"Repeat"
                            }
                        }""".replaceAll("\n", "").replaceAll(" ", ""));
            }
        }

        @Nested
        class DeserializationTest {
            @Test
            void should_deserialize_complete_message_with_complete_body() throws JsonProcessingException {
                var jsonMsg = """
                        {
                            "src":"1",
                            "dest":"3",
                            "body": {
                                "type":"echo",
                                "msg_id":14,
                                "in_reply_to":21,
                                "echo":"Repeat me"
                            }
                        }""";

                var message = mapper.readValue(jsonMsg, mapper.getTypeFactory().constructParametricType(Message.class, EchoBody.class));

                assertThat(message).isEqualTo(messageWithEchoBody("1", "3", body -> body
                        .withType("echo")
                        .withMsgId(14)
                        .withInReplyTo(21)
                        .withEcho("Repeat me")
                ));
            }

            @Test
            void should_not_deserialize_message_with_required_type_in_body() {
                var jsonMsg = """
                        {
                            "src":"1",
                            "dest":"3",
                            "body": {
                                "msg_id":14,
                                "in_reply_to":21,
                                "echo":"Repeat me"
                            }
                        }""";

                assertThatThrownBy(() -> mapper.readValue(jsonMsg, mapper.getTypeFactory().constructParametricType(Message.class, EchoBody.class)))
                        .isInstanceOf(NullPointerException.class)
                        .hasMessage("Cannot invoke \"com.fasterxml.jackson.databind.JsonNode.asText()\" because the return value of \"com.fasterxml.jackson.databind.JsonNode.get(String)\" is null");
            }

            @Test
            void should_deserialize_complete_message_with_not_complete_body() throws JsonProcessingException {
                var jsonMsg = """
                        {
                            "src":"1",
                            "dest":"3",
                            "body": {
                                "type":"echo",
                                "msg_id":14,
                                "echo":"Repeat me"
                            }
                        }""";

                var message = mapper.readValue(jsonMsg, Message.class);

                assertThat(message).isEqualTo(messageWithEchoBody("1", "3", body -> body
                        .withType("echo")
                        .withMsgId(14)
                        .withEcho("Repeat me")
                ));
            }
        }
    }

    @Nested
    class UniqueBodyTest {
        @Nested
        class SerializationTest {
            @Test
            void should_serialize_complete_message_with_complete_body() throws JsonProcessingException {
                var randomUUID = randomUUID();
                var echoMessage = messageWithUniqueBody("1", "2", body -> body
                        .withType("echo")
                        .withMsgId(12)
                        .withInReplyTo(19)
                        .withId(randomUUID)
                );

                var json = mapper.writeValueAsString(echoMessage);

                assertThat(json).isEqualTo("""
                        {
                            "src":"1",
                            "dest":"2",
                            "body":{
                                "type":"echo",
                                "msg_id":12,
                                "in_reply_to":19,
                                "id":"%s"
                            }
                        }""".formatted(randomUUID).replaceAll("\n", "").replaceAll(" ", ""));
            }

            @Test
            void should_serialize_complete_message_with_not_complete_body() throws JsonProcessingException {
                var randomUUID = randomUUID();
                var echoMessage = messageWithUniqueBody("1", "2", body -> body
                        .withType("echo")
                        .withMsgId(12)
                        .withId(randomUUID)
                );

                var json = mapper.writeValueAsString(echoMessage);

                assertThat(json).isEqualTo("""
                        {
                            "src":"1",
                            "dest":"2",
                            "body":{
                                "type":"echo",
                                "msg_id":12,
                                "id":"%s"
                            }
                        }""".formatted(randomUUID).replaceAll("\n", "").replaceAll(" ", ""));
            }
        }

        @Nested
        class DeserializationTest {
            @Test
            void should_deserialize_complete_message_with_complete_body() throws JsonProcessingException {
                var randomUUID = randomUUID();
                var jsonMsg = """
                        {
                            "src":"1",
                            "dest":"3",
                            "body": {
                                "type":"generate",
                                "msg_id":14,
                                "in_reply_to":21,
                                "id":"%s"
                            }
                        }""".formatted(randomUUID);

                var message = mapper.readValue(jsonMsg, mapper.getTypeFactory().constructParametricType(Message.class, EchoBody.class));

                assertThat(message).isEqualTo(messageWithUniqueBody("1", "3", body -> body
                        .withType("generate")
                        .withMsgId(14)
                        .withInReplyTo(21)
                        .withId(randomUUID)
                ));
            }

            @Test
            void should_not_deserialize_message_with_required_type_in_body() {
                var randomUUID = randomUUID();
                var jsonMsg = """
                        {
                            "src":"1",
                            "dest":"3",
                            "body": {
                                "msg_id":14,
                                "in_reply_to":21,
                                "id":"%s"
                            }
                        }""".formatted(randomUUID);

                assertThatThrownBy(() -> mapper.readValue(jsonMsg, mapper.getTypeFactory().constructParametricType(Message.class, EchoBody.class)))
                        .isInstanceOf(NullPointerException.class)
                        .hasMessage("Cannot invoke \"com.fasterxml.jackson.databind.JsonNode.asText()\" because the return value of \"com.fasterxml.jackson.databind.JsonNode.get(String)\" is null");
            }

            @Test
            void should_deserialize_complete_message_with_not_complete_body() throws JsonProcessingException {
                var randomUUID = randomUUID();
                var jsonMsg = """
                        {
                            "src":"1",
                            "dest":"3",
                            "body": {
                                "type":"generate",
                                "msg_id":14,
                                "id":"%s"
                            }
                        }""".formatted(randomUUID);

                var message = mapper.readValue(jsonMsg, Message.class);

                assertThat(message).isEqualTo(messageWithUniqueBody("1", "3", body -> body
                        .withType("generate")
                        .withMsgId(14)
                        .withId(randomUUID)
                ));
            }
        }
    }
}