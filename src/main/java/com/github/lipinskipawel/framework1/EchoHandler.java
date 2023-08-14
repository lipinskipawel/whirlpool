package com.github.lipinskipawel.framework1;

import com.github.lipinskipawel.base.Echo;
import com.github.lipinskipawel.base.EchoOk;
import com.github.lipinskipawel.base.Init;
import com.github.lipinskipawel.base.InitOk;

import static com.github.lipinskipawel.framework1.Server.writeRequest;

final class EchoHandler {

    void handle(Event<?> event) {
        final var body = event.body;
        if (body instanceof Init init) {
            final var response = new Event<>(new InitOk());
            response.src = event.dst;
            response.dst = event.src;
            response.body.msgId = 1;
            response.body.inReplyTo = init.msgId;
            System.out.println(writeRequest(response));
            return;
        }
        if (body instanceof Echo echo) {
            final var response = new Event<>(new EchoOk(echo.echo));
            response.src = event.dst;
            response.dst = event.src;
            response.body.msgId = echo.msgId + 1;
            response.body.inReplyTo = echo.msgId;
            System.out.println(writeRequest(response));
        }
    }
}
