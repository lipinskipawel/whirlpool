package com.github.lipinskipawel.base;

public sealed interface UniqueWorkload extends BaseWorkload permits Init, Quit,
        Unique {
}
