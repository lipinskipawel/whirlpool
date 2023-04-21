package com.github.lipinskipawel.protocol;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class MessageTest implements WithAssertions {

    private final ObjectMapper mapper = new ObjectMapper();

    @Nested
    class SerializationTest {
        @Test
        void should_serialize_complete_message_with_complete_body() throws JsonProcessingException {
            var echoBody = new Body.Builder()
                    .withType("echo")
                    .withMsgId(12)
                    .withInReplyTo(19)
                    .withEcho("echo")
                    .build();
            var msg = new Message("1", "2", echoBody);

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
            var echoBody = new Body.Builder()
                    .withType("echo")
                    .withMsgId(12)
                    .withEcho("Repeat")
                    .build();
            var msg = new Message("1", "2", echoBody);

            var json = mapper.writeValueAsString(msg);

            assertThat(json).isEqualTo("""
                    {
                        "src":"1",
                        "dest":"2",
                        "body":{
                            "type":"echo",
                            "msg_id":12,
                            "in_reply_to":0,
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

            var message = mapper.readValue(jsonMsg, Message.class);

            assertThat(message).isEqualTo(new Message("1", "3", new Body.Builder()
                    .withType("echo")
                    .withMsgId(14)
                    .withInReplyTo(21)
                    .withEcho("Repeat me")
                    .build()));
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

            assertThat(message).isEqualTo(new Message("1", "3", new Body.Builder()
                    .withType("echo")
                    .withMsgId(14)
                    .withInReplyTo(0)
                    .withEcho("Repeat me")
                    .build()));
        }
    }
}