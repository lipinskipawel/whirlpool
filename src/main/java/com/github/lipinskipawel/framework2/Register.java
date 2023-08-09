package com.github.lipinskipawel.framework2;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.github.lipinskipawel.framework2.protocol.Echo;
import com.github.lipinskipawel.framework2.protocol.EchoOk;
import com.github.lipinskipawel.framework2.protocol.Init;
import com.github.lipinskipawel.framework2.protocol.InitOk;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public final class Register {
    private static final ObjectMapper mapper = new ObjectMapper();
    private final EchoHandler handler = new EchoHandler();


    static final Map<String, JavaType> subTypes = createAllowedSubTypes();

    private static Map<String, JavaType> createAllowedSubTypes() {
        final var result = new HashMap<String, JavaType>();
        result.put("echo", TypeFactory.defaultInstance().constructFromCanonical(Echo.class.getCanonicalName()));
        result.put("echo_ok", TypeFactory.defaultInstance().constructFromCanonical(EchoOk.class.getCanonicalName()));
        result.put("init", TypeFactory.defaultInstance().constructFromCanonical(Init.class.getCanonicalName()));
        result.put("init_ok", TypeFactory.defaultInstance().constructFromCanonical(InitOk.class.getCanonicalName()));
        return result;
    }

    public static void configure(String type, JavaType javaType) {
        subTypes.put(type, javaType);
    }

    public void loop() {
        try (var scanner = new Scanner(System.in)) {
            final var initRequest = scanner.nextLine();
            final var initMessage = readRequest(initRequest);

            handler.handle(initMessage);
            while (scanner.hasNextLine()) {
                final var request = scanner.nextLine();
                handler.handle(readRequest(request));
            }
        }
    }

    public static Event readRequest(String request) {
        try {
            return mapper.readValue(request, Event.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public static String writeRequest(Event event) {
        try {
            return mapper.writeValueAsString(event);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
