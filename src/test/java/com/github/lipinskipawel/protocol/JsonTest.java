package com.github.lipinskipawel.protocol;

import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class JsonTest implements WithAssertions {

    @Nested
    class ToObjectTest {
    }

    @Nested
    class ToJsonTest {
    }

    @Nested
    class TypeOfJsonTest {
        @Test
        void should_correctly_read_type() {
            var json = """
                    {
                        "src":"n1",
                        "dest":"c2",
                        "body":{
                            "type":"broadcast",
                            "in_reply_to":"12"
                        }
                    }""".replaceAll("\n", "").replaceAll(" ", "");

            final var type = Json.typeOfJson(json);

            assertThat(type).isEqualTo("broadcast");
        }
    }
}