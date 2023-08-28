package com.github.lipinskipawel.whirlpool.protocol;

public final class Quit extends EventType implements EchoWorkload, UniqueWorkload, BroadcastWorkload {
    public Quit() {
        super("quit");
    }
}
