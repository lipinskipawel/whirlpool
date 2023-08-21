package com.github.lipinskipawel.base;

public sealed interface BaseWorkload permits EventType,
        EchoWorkload, BroadcastWorkload {

    int msgId();

    int inReplyTo();
}
