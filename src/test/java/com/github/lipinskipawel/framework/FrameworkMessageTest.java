package com.github.lipinskipawel.framework;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.github.lipinskipawel.framework.BuiltInBodies.Init;
import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;

import static com.github.lipinskipawel.framework.BuiltInBodies.Echo;
import static com.github.lipinskipawel.framework.FrameworkJson.configureObjectMapper;
import static com.github.lipinskipawel.framework.FrameworkJson.mapper;
import static com.github.lipinskipawel.framework.FrameworkMessage.Builder.frameworkMessage;
import static java.util.Optional.empty;
import static java.util.Optional.of;
import static java.util.UUID.randomUUID;

public class FrameworkMessageTest implements WithAssertions {

    @Nested
    class InitTest {
        @Test
        void should_deserialize_init_message() throws JsonProcessingException {
            var json = """
                    {
                        "id":12,
                        "src":"c1",
                        "dest":"n2",
                        "body":{
                            "type":"init",
                            "node_id":"n0",
                            "msg_id":10,
                            "in_reply_to":8
                        }
                    }""";

            final var object = mapper().readValue(json, new TypeReference<FrameworkMessage<Init>>() {
            });

            assertThat(object)
                    .isInstanceOf(FrameworkMessage.class)
                    .isEqualTo(frameworkMessage()
                            .withId(12)
                            .withSrc("c1")
                            .withDst("n2")
                            .withBody(new Init("n0", List.of()))
                            .withType("init")
                            .withMsgId(10)
                            .withInReplyTo(8)
                            .build());
        }

        @Test
        void should_serialize_init_message() throws JsonProcessingException {
            final var message = frameworkMessage()
                    .withId(12)
                    .withSrc("c1")
                    .withDst("n2")
                    .withBody(new Init())
                    .withType("init_ok")
                    .withMsgId(123)
                    .withInReplyTo(191)
                    .build();

            final var json = mapper().writeValueAsString(message);

            assertThat(json).isEqualTo("""
                    {
                        "id":12,
                        "src":"c1",
                        "dest":"n2",
                        "body":{
                            "type":"init_ok",
                            "msg_id":123,
                            "in_reply_to":191
                        }
                    }""".replaceAll("\n", "").replaceAll(" ", ""));
        }
    }

    @Nested
    class EchoTest {
        @Test
        void should_deserialize_echo_message() throws JsonProcessingException {
            var json = """
                    {
                        "id":12,
                        "src":"1",
                        "dest":"2",
                        "body":{
                            "type":"echo",
                            "msg_id":12,
                            "in_reply_to":19,
                            "echo":"please-echo"
                        }
                    }""";

            final var mapper = configureObjectMapper(Echo.class, new FrameworkEchoDeserializer(), new FrameworkEchoSerializer());
            final var object = mapper.readValue(json, new TypeReference<FrameworkMessage<Echo>>() {
            });

            assertThat(object)
                    .isInstanceOf(FrameworkMessage.class)
                    .isEqualTo(frameworkMessage()
                            .withId(12)
                            .withSrc("1")
                            .withDst("2")
                            .withBody(new Echo("please-echo"))
                            .withType("echo")
                            .withMsgId(12)
                            .withInReplyTo(19)
                            .build());
        }

        @Test
        void should_serialize_echo_message() throws JsonProcessingException {
            final var message = frameworkMessage()
                    .withId(12)
                    .withSrc("1")
                    .withDst("2")
                    .withBody(new Echo("please-echo"))
                    .withType("echo_ok")
                    .withMsgId(12)
                    .withInReplyTo(19)
                    .build();

            final var mapper = configureObjectMapper(Echo.class, new FrameworkEchoDeserializer(), new FrameworkEchoSerializer());
            final var json = mapper.writeValueAsString(message);

            assertThat(json).isEqualTo("""
                    {
                        "id":12,
                        "src":"1",
                        "dest":"2",
                        "body":{
                            "type":"echo_ok",
                            "msg_id":12,
                            "in_reply_to":19,
                            "echo":"please-echo"
                        }
                    }""".replaceAll("\n", "").replaceAll(" ", ""));
        }
    }

    @Nested
    class UniqueTest {
        @Test
        void should_deserialize_unique_message() throws JsonProcessingException {
            var json = """
                    {
                        "id":12,
                        "src":"1",
                        "dest":"2",
                        "body":{
                            "type":"generate",
                            "msg_id":12,
                            "in_reply_to":19
                        }
                    }""";

            final var mapper = configureObjectMapper(Unique.class, new FrameworkUniqueDeserializer(), new FrameworkUniqueSerializer());
            final var object = mapper.readValue(json, new TypeReference<FrameworkMessage<Unique>>() {
            });

            assertThat(object)
                    .isInstanceOf(FrameworkMessage.class)
                    .isEqualTo(frameworkMessage()
                            .withId(12)
                            .withSrc("1")
                            .withDst("2")
                            .withBody(new Unique(empty()))
                            .withType("generate")
                            .withMsgId(12)
                            .withInReplyTo(19)
                            .build());
        }

        @Test
        void should_serialize_echo_message() throws JsonProcessingException {
            final var uuid = randomUUID();
            final var uuidString = uuid.toString();
            final var message = frameworkMessage()
                    .withId(12)
                    .withSrc("1")
                    .withDst("2")
                    .withBody(new Unique(of(uuid)))
                    .withType("generate_ok")
                    .withMsgId(12)
                    .withInReplyTo(19)
                    .build();

            final var mapper = configureObjectMapper(Unique.class, new FrameworkUniqueDeserializer(), new FrameworkUniqueSerializer());
            final var json = mapper.writeValueAsString(message);

            assertThat(json).isEqualTo("""
                    {
                        "id":12,
                        "src":"1",
                        "dest":"2",
                        "body":{
                            "type":"generate_ok",
                            "msg_id":12,
                            "in_reply_to":19,
                            "id":"%s"
                        }
                    }""".formatted(uuidString).replaceAll("\n", "").replaceAll(" ", ""));
        }
    }
}
