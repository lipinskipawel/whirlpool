package com.github.lipinskipawel.framework2;

import com.github.lipinskipawel.framework2.protocol.Echo;
import com.github.lipinskipawel.framework2.protocol.EchoOk;
import com.github.lipinskipawel.framework2.protocol.Init;
import com.github.lipinskipawel.framework2.protocol.InitOk;

import static com.github.lipinskipawel.framework2.Register.writeRequest;

final class EchoHandler {

    void handle(Event event) {
        final var body = event.body;
        if (body instanceof Init) {
            final var response = new Event(new InitOk());
            response.src = event.dst;
            response.dst = event.src;
            response.body.msgId = 1;
            response.body.inReplyTo = event.body.msgId;
            System.out.println(writeRequest(response));
            return;
        }
        if (body instanceof Echo echo) {
            final var response = new Event(new EchoOk(echo.echo));
            response.src = event.dst;
            response.dst = event.src;
            response.body.msgId = event.body.msgId + 1;
            response.body.inReplyTo = event.body.msgId;
            System.out.println(writeRequest(response));
        }
    }
}
