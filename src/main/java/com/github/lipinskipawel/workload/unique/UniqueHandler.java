package com.github.lipinskipawel.workload.unique;

import com.github.lipinskipawel.maelstrom.framework.Event;
import com.github.lipinskipawel.maelstrom.framework.EventHandler;
import com.github.lipinskipawel.maelstrom.protocol.Init;
import com.github.lipinskipawel.maelstrom.protocol.InitOk;
import com.github.lipinskipawel.maelstrom.protocol.Quit;
import com.github.lipinskipawel.maelstrom.protocol.unique.Unique;
import com.github.lipinskipawel.maelstrom.protocol.unique.UniqueOk;
import com.github.lipinskipawel.maelstrom.protocol.unique.UniqueWorkload;

import static java.util.UUID.randomUUID;

/**
 * ./maelstrom test -w unique-ids --bin whirlpool/whirlpool.sh --time-limit 30 --rate 1000 --node-count 3 \
 *     --availability total --nemesis partition --log-stderr
 */
public final class UniqueHandler extends EventHandler<UniqueWorkload> {

    @Override
    public void handle(Event<UniqueWorkload> event) {
        switch (event.body) {
            case Init __ -> replyAndSend(event, new InitOk());
            case Unique __ -> replyAndSend(event, new UniqueOk(randomUUID()));
            case Quit __ -> {
            }
        }
    }
}
