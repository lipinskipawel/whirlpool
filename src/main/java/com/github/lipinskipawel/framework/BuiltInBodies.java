package com.github.lipinskipawel.framework;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public final class BuiltInBodies {

    public record Init(String nodeId, List<String> nodeIds) {
        public Init() {
            this("", List.of());
        }
    }

    public record Echo(String echo) {
    }

    public record Unique(Optional<UUID> id) {
    }

    public record Topology() {
    }

    public record Read(List<Integer> messages) {
    }
}
