package com.github.lipinskipawel.workload.unique;

import com.github.lipinskipawel.whirlpool.protocol.Init;
import com.github.lipinskipawel.whirlpool.protocol.InitOk;
import com.github.lipinskipawel.whirlpool.protocol.Unique;
import com.github.lipinskipawel.whirlpool.protocol.UniqueOk;
import com.github.lipinskipawel.whirlpool.protocol.UniqueWorkload;
import com.github.lipinskipawel.whirlpool.framework.Event;
import com.github.lipinskipawel.whirlpool.framework.EventHandler;

import static java.util.UUID.randomUUID;

final class UniqueHandler extends EventHandler<UniqueWorkload> {

    @Override
    public void handle(Event<UniqueWorkload> event) {
        final var body = event.body;
        if (body instanceof Init) {
            replyAndSend(event, new InitOk());
        }
        if (body instanceof Unique) {
            replyAndSend(event, new UniqueOk(randomUUID()));
        }
    }
}
