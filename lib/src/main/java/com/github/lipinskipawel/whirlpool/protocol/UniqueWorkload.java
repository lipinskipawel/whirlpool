package com.github.lipinskipawel.whirlpool.protocol;

public sealed interface UniqueWorkload extends BaseWorkload permits Init, Quit,
        Unique {
}
