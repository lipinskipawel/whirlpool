package com.github.lipinskipawel.protocol;

import com.fasterxml.jackson.core.type.TypeReference;
import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;

import static com.github.lipinskipawel.protocol.Json.toJson;
import static com.github.lipinskipawel.protocol.Json.toObject;
import static com.github.lipinskipawel.protocol.Message.messageWithEchoBody;
import static com.github.lipinskipawel.protocol.Message.messageWithInitBody;
import static com.github.lipinskipawel.protocol.Message.messageWithUniqueBody;
import static java.util.UUID.randomUUID;

class MessageTest implements WithAssertions {

    @Nested
    class InitEchoBodyTest {
        @Nested
        class SerializationTest {
            @Test
            void should_serialize_complete_message_with_complete_init_body() {
                var initMessage = messageWithInitBody(123, "1", "2", body -> body
                        .withType("init")
                        .withMsgId(14)
                        .withInReplyTo(12)
                        .withNodeId("n1")
                        .withNodeIds(List.of("n1", "n2", "n3"))
                );

                var json = toJson(initMessage);

                assertThat(json).isEqualTo("""
                        {
                            "id":123,
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
            void should_serialize_complete_message_with_not_complete_init_body() {
                var initMessage = messageWithInitBody(145, "1", "2", body -> body
                        .withType("init")
                        .withMsgId(12)
                        .withNodeId("n1")
                        .withNodeIds(List.of("n1", "n2", "n3"))
                );

                var json = toJson(initMessage);

                assertThat(json).isEqualTo("""
                        {
                            "id":145,
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
            void should_deserialize_complete_message_with_complete_init_body() {
                var jsonMsg = """
                        {
                            "id":123,
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

                var message = toObject(jsonMsg, new TypeReference<Message<InitBody>>() {
                });

                assertThat(message).isEqualTo(messageWithInitBody(123, "1", "3", body -> body
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
                        "id":1,
                        "src":"1",
                        "dest":"3",
                        "body": {
                            "msg_id":14,
                            "in_reply_to":133,
                            "node_id":"n1",
                            "node_ids":["n1","n2","n3"]
                        }
                    }""";

            assertThatThrownBy(() -> toObject(jsonMsg, new TypeReference<Message<InitBody>>() {
            }))
                    .isInstanceOf(NullPointerException.class)
                    .hasMessage("Cannot invoke \"com.fasterxml.jackson.databind.JsonNode.asText()\" because the return value of \"com.fasterxml.jackson.databind.JsonNode.get(String)\" is null");
        }

        @Test
        void should_deserialize_complete_message_with_not_complete_init_body() {
            var jsonMsg = """
                    {
                        "id":1,
                        "src":"1",
                        "dest":"3",
                        "body": {
                            "type":"init",
                            "msg_id":14,
                            "node_id":"n1",
                            "node_ids":["n1","n2","n3"]
                        }
                    }""";

            var message = toObject(jsonMsg, new TypeReference<Message<InitBody>>() {
            });

            assertThat(message).isEqualTo(messageWithInitBody(1, "1", "3", body -> body
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
            void should_serialize_complete_message_with_complete_body() {
                var echoMessage = messageWithEchoBody(12, "1", "2", body -> body
                        .withType("echo")
                        .withMsgId(12)
                        .withInReplyTo(19)
                        .withEcho("echo")
                );

                var json = toJson(echoMessage);

                assertThat(json).isEqualTo("""
                        {
                            "id":12,
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
            void should_serialize_complete_message_with_not_complete_body() {
                var echoMessage = messageWithEchoBody(12, "1", "2", body -> body
                        .withType("echo")
                        .withMsgId(12)
                        .withEcho("Repeat")
                );

                var json = toJson(echoMessage);

                assertThat(json).isEqualTo("""
                        {
                            "id":12,
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
            void should_deserialize_complete_message_with_complete_body() {
                var jsonMsg = """
                        {
                            "id":"0",
                            "src":"1",
                            "dest":"3",
                            "body": {
                                "type":"echo",
                                "msg_id":14,
                                "in_reply_to":21,
                                "echo":"Repeat me"
                            }
                        }""";

                var message = toObject(jsonMsg, new TypeReference<Message<EchoBody>>() {
                });

                assertThat(message).isEqualTo(messageWithEchoBody(0, "1", "3", body -> body
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
                            "id":"0",
                            "src":"1",
                            "dest":"3",
                            "body": {
                                "msg_id":14,
                                "in_reply_to":21,
                                "echo":"Repeat me"
                            }
                        }""";

                assertThatThrownBy(() -> toObject(jsonMsg, new TypeReference<Message<EchoBody>>() {
                }))
                        .isInstanceOf(NullPointerException.class)
                        .hasMessage("Cannot invoke \"com.fasterxml.jackson.databind.JsonNode.asText()\" because the return value of \"com.fasterxml.jackson.databind.JsonNode.get(String)\" is null");
            }

            @Test
            void should_deserialize_complete_message_with_not_complete_body() {
                var jsonMsg = """
                        {
                            "id":"0",
                            "src":"1",
                            "dest":"3",
                            "body": {
                                "type":"echo",
                                "msg_id":14,
                                "echo":"Repeat me"
                            }
                        }""";

                var message = toObject(jsonMsg, new TypeReference<Message<EchoBody>>() {
                });

                assertThat(message).isEqualTo(messageWithEchoBody(0, "1", "3", body -> body
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
            void should_serialize_complete_message_with_complete_body() {
                var randomUUID = randomUUID();
                var echoMessage = messageWithUniqueBody(0, "1", "2", body -> body
                        .withType("echo")
                        .withMsgId(12)
                        .withInReplyTo(19)
                        .withId(randomUUID)
                );

                var json = toJson(echoMessage);

                assertThat(json).isEqualTo("""
                        {
                            "id":0,
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
            void should_serialize_complete_message_with_not_complete_body() {
                var randomUUID = randomUUID();
                var echoMessage = messageWithUniqueBody(0, "1", "2", body -> body
                        .withType("echo")
                        .withMsgId(12)
                        .withId(randomUUID)
                );

                var json = toJson(echoMessage);

                assertThat(json).isEqualTo("""
                        {
                            "id":0,
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
            void should_deserialize_complete_message_with_complete_body() {
                var randomUUID = randomUUID();
                var jsonMsg = """
                        {
                            "id":"0",
                            "src":"1",
                            "dest":"3",
                            "body": {
                                "type":"generate",
                                "msg_id":14,
                                "in_reply_to":21,
                                "id":"%s"
                            }
                        }""".formatted(randomUUID);

                var message = toObject(jsonMsg, new TypeReference<Message<EchoBody>>() {
                });

                assertThat(message).isEqualTo(messageWithUniqueBody(0, "1", "3", body -> body
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
                            "id":"0",
                            "src":"1",
                            "dest":"3",
                            "body": {
                                "msg_id":14,
                                "in_reply_to":21,
                                "id":"%s"
                            }
                        }""".formatted(randomUUID);

                assertThatThrownBy(() -> toObject(jsonMsg, new TypeReference<Message<EchoBody>>() {
                }))
                        .isInstanceOf(NullPointerException.class)
                        .hasMessage("Cannot invoke \"com.fasterxml.jackson.databind.JsonNode.asText()\" because the return value of \"com.fasterxml.jackson.databind.JsonNode.get(String)\" is null");
            }

            @Test
            void should_deserialize_complete_message_with_not_complete_body() {
                var randomUUID = randomUUID();
                var jsonMsg = """
                        {
                            "id":"0",
                            "src":"1",
                            "dest":"3",
                            "body": {
                                "type":"generate",
                                "msg_id":14,
                                "id":"%s"
                            }
                        }""".formatted(randomUUID);

                var message = toObject(jsonMsg, new TypeReference<Message<UniqueBody>>() {
                });

                assertThat(message).isEqualTo(messageWithUniqueBody(0, "1", "3", body -> body
                        .withType("generate")
                        .withMsgId(14)
                        .withId(randomUUID)
                ));
            }
        }
    }
}