package com.github.lipinskipawel.base;

public final class Quit extends EventType implements EchoWorkload, BroadcastWorkload {
    public Quit() {
        super("quit");
    }
}
