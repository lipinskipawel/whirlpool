package com.github.lipinskipawel.framework;

import java.util.List;

public final class BuiltInBodies {

    public record Init(String nodeId, List<String> nodeIds) {
        public Init() {
            this("", List.of());
        }
    }

    public record Echo(String echo) {
    }
}
