package com.github.lipinskipawel.framework1;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.github.lipinskipawel.base.BaseWorkload;
import com.github.lipinskipawel.base.Broadcast;
import com.github.lipinskipawel.base.BroadcastOk;
import com.github.lipinskipawel.base.Echo;
import com.github.lipinskipawel.base.EchoOk;
import com.github.lipinskipawel.base.EventType;
import com.github.lipinskipawel.base.Init;
import com.github.lipinskipawel.base.InitOk;
import com.github.lipinskipawel.base.Quit;
import com.github.lipinskipawel.base.Read;
import com.github.lipinskipawel.base.ReadOk;
import com.github.lipinskipawel.base.Topology;
import com.github.lipinskipawel.base.TopologyOk;
import com.github.lipinskipawel.base.Unique;
import com.github.lipinskipawel.base.UniqueOk;

import java.util.HashMap;
import java.util.Map;

final class JsonSupport {
    private static final Map<String, Class<? extends EventType>> POSSIBLE_TYPES = createMappings();
    private static ObjectMapper staticMapper = new ObjectMapper();
    private final ObjectMapper mapper;

    JsonSupport(Map<String, Class<? extends EventType>> customTypes) {
        POSSIBLE_TYPES.putAll(customTypes);
        mapper = new ObjectMapper().registerModule(
                new SimpleModule().addDeserializer(Event.class, new EventDeserializer(POSSIBLE_TYPES))
        );
        staticMapper = mapper.copy();
    }

    @SuppressWarnings("unchecked")
    public <T extends BaseWorkload> Event<T> readRequest(String request) {
        try {
            return mapper.readValue(request, Event.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public static String writeRequest(Event<?> event) {
        try {
            return staticMapper.writeValueAsString(event);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    private static Map<String, Class<? extends EventType>> createMappings() {
        final var mappings = new HashMap<String, Class<? extends EventType>>();
        mappings.put("init", Init.class);
        mappings.put("init_ok", InitOk.class);
        mappings.put("quit", Quit.class);
        mappings.put("echo", Echo.class);
        mappings.put("echo_ok", EchoOk.class);
        mappings.put("generate", Unique.class);
        mappings.put("generate_ok", UniqueOk.class);
        mappings.put("broadcast", Broadcast.class);
        mappings.put("broadcast_ok", BroadcastOk.class);
        mappings.put("read", Read.class);
        mappings.put("read_ok", ReadOk.class);
        mappings.put("topology", Topology.class);
        mappings.put("topology_ok", TopologyOk.class);
        return mappings;
    }
}
