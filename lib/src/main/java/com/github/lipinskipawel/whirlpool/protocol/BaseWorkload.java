package com.github.lipinskipawel.whirlpool.protocol;

public sealed interface BaseWorkload permits EventType,
        EchoWorkload, UniqueWorkload, BroadcastWorkload {

    int msgId();

    int inReplyTo();
}
