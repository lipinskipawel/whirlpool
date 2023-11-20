package com.github.lipinskipawel.whirlpool.workload.echo;

import com.github.lipinskipawel.maelstrom.api.framework.EventHandler;
import com.github.lipinskipawel.maelstrom.api.protocol.Event;
import com.github.lipinskipawel.maelstrom.api.protocol.Init;
import com.github.lipinskipawel.maelstrom.api.protocol.InitOk;
import com.github.lipinskipawel.maelstrom.api.protocol.Quit;
import com.github.lipinskipawel.maelstrom.api.protocol.echo.Echo;
import com.github.lipinskipawel.maelstrom.api.protocol.echo.EchoOk;
import com.github.lipinskipawel.maelstrom.api.protocol.echo.EchoWorkload;

/**
 * ./maelstrom test -w broadcast --bin whirlpool/whirlpool.sh --node-count 1 --time-limit 10 --log-stderr
 */
public final class EchoHandler extends EventHandler<EchoWorkload> {

    @Override
    public void handle(Event<EchoWorkload> event) {
        switch (event.body) {
            case Init __ -> send(event.reply(new InitOk()));
            case Echo echo -> send(event.reply(new EchoOk(echo.echo)));
            case Quit __ -> {
            }
        }
    }
}
