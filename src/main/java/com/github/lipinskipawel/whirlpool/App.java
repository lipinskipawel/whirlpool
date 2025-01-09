package com.github.lipinskipawel.whirlpool;

import com.github.lipinskipawel.whirlpool.workload.broadcast.BroadcastHandler;
import com.github.lipinskipawel.whirlpool.workload.broadcast.Internal;

import java.util.Map;

import static com.github.lipinskipawel.maelstrom.api.framework.ServerBuilder.server;

/**
 * Run server with logs
 * java -jar lib/maelstrom.jar serve
 */
public class App {
    public static void main(String[] args) {
        server()
                .broadcast(new BroadcastHandler())
                .customEvents(Map.of("internal", Internal.class))
                .build()
                .run();
    }
}
