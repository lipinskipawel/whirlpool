package com.github.lipinskipawel.base;

public sealed interface EchoWorkload permits Init, InitOk, Quit,
        Echo, EchoOk {
}
