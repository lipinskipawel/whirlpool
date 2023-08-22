package com.github.lipinskipawel.framework1;

import com.github.lipinskipawel.base.Init;
import com.github.lipinskipawel.base.InitOk;
import com.github.lipinskipawel.base.Unique;
import com.github.lipinskipawel.base.UniqueOk;
import com.github.lipinskipawel.base.UniqueWorkload;

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
