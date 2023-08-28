package com.github.lipinskipawel.whirlpool.protocol;

import java.util.UUID;

public final class UniqueOk extends EventType {
    public UUID id;

    public UniqueOk(UUID uuid) {
        super("generate_ok");
        this.id = uuid;
    }
}
