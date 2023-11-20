package com.github.lipinskipawel.whirlpool.workload.unique;

import com.github.lipinskipawel.maelstrom.api.framework.EventHandler;
import com.github.lipinskipawel.maelstrom.api.protocol.Event;
import com.github.lipinskipawel.maelstrom.api.protocol.Init;
import com.github.lipinskipawel.maelstrom.api.protocol.InitOk;
import com.github.lipinskipawel.maelstrom.api.protocol.Quit;
import com.github.lipinskipawel.maelstrom.api.protocol.unique.Unique;
import com.github.lipinskipawel.maelstrom.api.protocol.unique.UniqueOk;
import com.github.lipinskipawel.maelstrom.api.protocol.unique.UniqueWorkload;

import static java.util.UUID.randomUUID;

/**
 * ./maelstrom test -w unique-ids --bin whirlpool/whirlpool.sh --time-limit 30 --rate 1000 --node-count 3 \
 *     --availability total --nemesis partition --log-stderr
 */
public final class UniqueHandler extends EventHandler<UniqueWorkload> {

    @Override
    public void handle(Event<UniqueWorkload> event) {
        switch (event.body) {
            case Init __ -> send(event.reply(new InitOk()));
            case Unique __ -> send(event.reply(new UniqueOk(randomUUID())));
            case Quit __ -> {
            }
        }
    }
}
