package com.github.lipinskipawel.base;

public sealed interface EchoWorkload extends BaseWorkload permits Init, Quit,
        Echo {
}
