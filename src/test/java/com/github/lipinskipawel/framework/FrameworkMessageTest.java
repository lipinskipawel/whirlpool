package com.github.lipinskipawel.framework;

import com.fasterxml.jackson.core.type.TypeReference;
import com.github.lipinskipawel.protocol.Json;
import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.Test;

import static com.github.lipinskipawel.framework.FrameworkMessage.Builder.frameworkMessage;

public class FrameworkMessageTest implements WithAssertions {

    @Test
    void should_deserialize_echo_message() {
        var json = """
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
                }""";
        final var object = Json.toObject(json, new TypeReference<FrameworkMessage<FrameworkEchoBody>>() {
        });

        assertThat(object)
                .isInstanceOf(FrameworkMessage.class)
                .isEqualTo(frameworkMessage()
                        .withId(12)
                        .withSrc("1")
                        .withDst("2")
                        .withBody(new FrameworkEchoBody("echo"))
                        .withType("echo")
                        .withMsgId(12)
                        .withInReplyTo(19)
                        .build());
    }

    @Test
    void should_serialize_echo_message() {
        final var message = frameworkMessage()
                .withId(12)
                .withSrc("1")
                .withDst("2")
                .withBody(new FrameworkEchoBody("echo"))
                .withType("echo")
                .withMsgId(12)
                .withInReplyTo(19)
                .build();

        final var json = Json.toJson(message);

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
}
