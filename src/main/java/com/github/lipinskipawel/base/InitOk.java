package com.github.lipinskipawel.base;

public final class InitOk extends EventType implements InitWorkload, EchoWorkload, BroadcastWorkload {

    public InitOk() {
        super("init_ok");
    }
}
