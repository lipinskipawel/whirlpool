package com.github.lipinskipawel.whirlpool;

import com.github.lipinskipawel.whirlpool.workload.broadcast.BroadcastHandler;
import com.github.lipinskipawel.whirlpool.workload.broadcast.Internal;

import java.util.Map;

import static com.github.lipinskipawel.maelstrom.api.framework.Server.workloadWithCustomEvent;

/**
 * Run server with logs
 * java -jar lib/maelstrom.jar serve
 */
public class App {
    public static void main(String[] args) {
        workloadWithCustomEvent(new BroadcastHandler(), Map.of("internal", Internal.class))
                .loop();
    }
}
