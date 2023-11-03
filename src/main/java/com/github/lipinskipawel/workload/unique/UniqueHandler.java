package com.github.lipinskipawel.workload.unique;

import io.github.lipinskipawel.maelstrom.framework.Event;
import io.github.lipinskipawel.maelstrom.framework.EventHandler;
import io.github.lipinskipawel.maelstrom.protocol.Init;
import io.github.lipinskipawel.maelstrom.protocol.InitOk;
import io.github.lipinskipawel.maelstrom.protocol.unique.Unique;
import io.github.lipinskipawel.maelstrom.protocol.unique.UniqueOk;
import io.github.lipinskipawel.maelstrom.protocol.unique.UniqueWorkload;

import static java.util.UUID.randomUUID;

/**
 * ./maelstrom test -w unique-ids --bin whirlpool/whirlpool.sh --time-limit 30 --rate 1000 --node-count 3 \
 *     --availability total --nemesis partition --log-stderr
 */
public final class UniqueHandler extends EventHandler<UniqueWorkload> {

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
