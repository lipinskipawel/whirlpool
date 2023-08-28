package com.github.lipinskipawel.whirlpool.protocol;

public sealed interface BroadcastWorkload extends BaseWorkload permits Init, Quit, CustomRequest,
        Broadcast, Read, Topology {
}
