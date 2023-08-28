package com.github.lipinskipawel.whirlpool.protocol;

public sealed interface EchoWorkload extends BaseWorkload permits Init, Quit,
        Echo {
}
