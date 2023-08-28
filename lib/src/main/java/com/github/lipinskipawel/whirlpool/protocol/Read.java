package com.github.lipinskipawel.whirlpool.protocol;

public final class Read extends EventType implements BroadcastWorkload {
    public Read() {
        super("read");
    }
}
