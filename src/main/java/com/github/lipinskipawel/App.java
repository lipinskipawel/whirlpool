package com.github.lipinskipawel;

import com.github.lipinskipawel.workload.broadcast.BroadcastHandler;
import com.github.lipinskipawel.workload.broadcast.Internal;

import java.util.Map;

import static io.github.lipinskipawel.maelstrom.framework.Server.workloadWithCustomEvent;

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
