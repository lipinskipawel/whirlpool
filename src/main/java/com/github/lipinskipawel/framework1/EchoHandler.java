package com.github.lipinskipawel.framework1;

import com.github.lipinskipawel.base.Echo;
import com.github.lipinskipawel.base.EchoOk;
import com.github.lipinskipawel.base.EchoWorkload;
import com.github.lipinskipawel.base.Init;
import com.github.lipinskipawel.base.InitOk;

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
