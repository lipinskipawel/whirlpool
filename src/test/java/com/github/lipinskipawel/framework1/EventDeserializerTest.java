package com.github.lipinskipawel.framework1;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.github.lipinskipawel.base.Echo;
import com.github.lipinskipawel.base.EchoOk;
import com.github.lipinskipawel.base.EchoWorkload;
import com.github.lipinskipawel.base.Init;
import com.github.lipinskipawel.base.InitOk;
import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.Test;

class EventDeserializerTest implements WithAssertions {

    private final ObjectMapper mapper = new ObjectMapper()
            .registerModule(new Jdk8Module())
            .registerModule(new SimpleModule()
                    .addDeserializer(Event.class, new EventDeserializer())
            );

    @Test
    void should_serialize_event() throws JsonProcessingException {
        var event = new Event<>();
        event.id = 1;
        event.src = "c2";
        event.dst = "n0";
        event.body = new InitOk();

        var json = mapper.writeValueAsString(event);

        assertThat(json).isEqualTo("""
                {
                    "id": 1,
                    "src": "c2",
                    "dest": "n0",
                    "body": {
                        "type": "init_ok",
                        "msg_id": 0,
                        "in_reply_to": 0
                    }
                }""".replaceAll("\n", "").replaceAll(" ", ""));
    }

    @Test
    void should_deserialize_event() throws JsonProcessingException {
        var json = """
                {
                    "id": 1,
                    "src": "c2",
                    "dest": "n0",
                    "body": {
                        "type" : "echo_ok",
                        "echo": "please-echo"
                    }
                }""";

        var event = mapper.readValue(json, Event.class);

        var expected = new Event<>();
        expected.id = 1;
        expected.src = "c2";
        expected.dst = "n0";
        expected.body = new EchoOk("please-echo");
        assertThat(event)
                .isInstanceOf(Event.class)
                .isEqualTo(expected);
    }

    @Test
    void should_deserialize_event_with_cast() throws JsonProcessingException {
        var json = """
                {
                    "id": 1,
                    "src": "c2",
                    "dest": "n0",
                    "body": {
                        "type" : "echo_ok",
                        "echo": "please-echo"
                    }
                }""";

        var event = mapper.readValue(json, Event.class);

        var expected = new Event<>();
        expected.id = 1;
        expected.src = "c2";
        expected.dst = "n0";
        expected.body = (EchoOk) event.body;
        assertThat(event)
                .isInstanceOf(Event.class)
                .isEqualTo(expected);
    }

    @Test
    void should_deserialize_event_with_type_reference() throws JsonProcessingException {
        var json = """
                {
                    "id": 1,
                    "src": "c2",
                    "dest": "n0",
                    "body": {
                        "type" : "echo_ok",
                        "echo": "please-echo"
                    }
                }""";

        var event = mapper.readValue(json, new TypeReference<Event<EchoOk>>() {
        });

        var expected = new Event<>();
        expected.id = 1;
        expected.src = "c2";
        expected.dst = "n0";
        expected.body = new EchoOk("please-echo");
        assertThat(event)
                .isInstanceOf(Event.class)
                .isEqualTo(expected);
    }

    @Test
    void should_deserialize_event_with_instance_of() throws JsonProcessingException {
        var json = """
                {
                    "id": 1,
                    "src": "c2",
                    "dest": "n0",
                    "body": {
                        "type" : "echo_ok",
                        "echo": "please-echo"
                    }
                }""";

        var event = mapper.readValue(json, Event.class);

        EchoWorkload echoBody = (EchoWorkload) event.body;
        if (echoBody instanceof Init) {
            fail("it is not a init event");
        }
        if (echoBody instanceof InitOk) {
            fail("it is not a init_ok event");
        }
        if (echoBody instanceof Echo) {
            fail("it is not a echo event");
        }

        var expected = new Event<>();
        expected.id = 1;
        expected.src = "c2";
        expected.dst = "n0";
        expected.body = new EchoOk("please-echo");
        assertThat(event)
                .isInstanceOf(Event.class)
                .isEqualTo(expected);
    }
}