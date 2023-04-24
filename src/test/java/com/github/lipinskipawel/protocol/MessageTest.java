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
import static java.util.Optional.empty;
import static java.util.Optional.of;

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
    class InitBodyTest {
        @Nested
        class SerializationTest {
            @Test
            void should_serialize_complete_message_with_complete_init_body() throws JsonProcessingException {
                var initBody = new InitBody("init", of(12), of(12), of("n1"), of(List.of("n1", "n2", "n3")));
                var msg = new Message<>("1", "2", initBody);

                var json = mapper.writeValueAsString(msg);

                assertThat(json).isEqualTo("""
                        {
                            "src":"1",
                            "dest":"2",
                            "body":{
                                "type":"init",
                                "msg_id":12,
                                "in_reply_to":12,
                                "node_id":"n1",
                                "node_ids":["n1","n2","n3"]
                            }
                        }""".replaceAll("\n", "").replaceAll(" ", ""));
            }

            @Test
            void should_serialize_complete_message_with_not_complete_init_body() throws JsonProcessingException {
                var initBody = new InitBody("init", of(12), empty(), of("n1"), of(List.of("n1", "n2", "n3")));
                var msg = new Message<>("1", "2", initBody);

                var json = mapper.writeValueAsString(msg);

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

                final var initBody = new InitBody("init", of(14), of(133), of("n1"), of(List.of("n1", "n2", "n3")));
                assertThat(message).isEqualTo(new Message<>("1", "3", initBody));
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

            final var initBody = new InitBody("init", of(14), empty(), of("n1"), of(List.of("n1", "n2", "n3")));
            assertThat(message).isEqualTo(new Message<>("1", "3", initBody));
        }
    }

    @Nested
    class BodyTest {
        @Nested
        class SerializationTest {
            @Test
            void should_serialize_complete_message_with_complete_body() throws JsonProcessingException {
                var echoBody = new Body("echo", 12, of(19), "echo");
                var msg = new Message<>("1", "2", echoBody);

                var json = mapper.writeValueAsString(msg);

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
                var echoBody = new Body("echo", 12, empty(), "Repeat");
                var msg = new Message<>("1", "2", echoBody);

                var json = mapper.writeValueAsString(msg);

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

                var message = mapper.readValue(jsonMsg, mapper.getTypeFactory().constructParametricType(Message.class, Body.class));

                assertThat(message).isEqualTo(
                        new Message<>("1", "3", new Body("echo", 14, of(21), "Repeat me")));
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

                assertThatThrownBy(() -> mapper.readValue(jsonMsg, mapper.getTypeFactory().constructParametricType(Message.class, Body.class)))
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

                assertThat(message).isEqualTo(
                        new Message<>("1", "3", new Body("echo", 14, empty(), "Repeat me")));
            }
        }
    }
}