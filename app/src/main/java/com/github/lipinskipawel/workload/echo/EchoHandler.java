package com.github.lipinskipawel.workload.echo;

import com.github.lipinskipawel.whirlpool.protocol.Echo;
import com.github.lipinskipawel.whirlpool.protocol.EchoOk;
import com.github.lipinskipawel.whirlpool.protocol.EchoWorkload;
import com.github.lipinskipawel.whirlpool.protocol.Init;
import com.github.lipinskipawel.whirlpool.protocol.InitOk;
import com.github.lipinskipawel.whirlpool.framework.Event;
import com.github.lipinskipawel.whirlpool.framework.EventHandler;

final class EchoHandler extends EventHandler<EchoWorkload> {

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
