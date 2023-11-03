package com.github.lipinskipawel.workload.echo;

import io.github.lipinskipawel.maelstrom.framework.Event;
import io.github.lipinskipawel.maelstrom.framework.EventHandler;
import io.github.lipinskipawel.maelstrom.protocol.Init;
import io.github.lipinskipawel.maelstrom.protocol.InitOk;
import io.github.lipinskipawel.maelstrom.protocol.echo.Echo;
import io.github.lipinskipawel.maelstrom.protocol.echo.EchoOk;
import io.github.lipinskipawel.maelstrom.protocol.echo.EchoWorkload;

/**
 * ./maelstrom test -w broadcast --bin lipinskipawel/whirlpool/whirlpool.sh --node-count 1 --time-limit 10 --log-stderr
 */
public final class EchoHandler extends EventHandler<EchoWorkload> {

    @Override
    public void handle(Event<EchoWorkload> event) {
        final var body = event.body;
        if (body instanceof Init) {
            replyAndSend(event, new InitOk());
            return;
        }
        if (body instanceof Echo echo) {
            replyAndSend(event, new EchoOk(echo.echo));
        }
    }
}
