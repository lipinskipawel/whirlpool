package com.github.lipinskipawel.base;

public sealed interface BroadcastWorkload permits Init, InitOk, Quit, CustomRequest,
        Broadcast, BroadcastOk, Read, ReadOk, Topology, TopologyOk {
}
