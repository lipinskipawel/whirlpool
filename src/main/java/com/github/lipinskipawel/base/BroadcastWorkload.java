package com.github.lipinskipawel.base;

public sealed interface BroadcastWorkload extends BaseWorkload permits Init, Quit, CustomRequest,
        Broadcast, Read, Topology {
}
