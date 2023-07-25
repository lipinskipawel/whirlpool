package com.github.lipinskipawel.framework;

import static com.github.lipinskipawel.framework.FrameworkMessage.Builder.frameworkMessage;

/**
 * Run echo maelstrom workload
 * ./maelstrom test -w echo --bin whirlpool.sh --node-count 1 --time-limit 10 --log-stderr
 */
public final class EchoHandler extends RequestHandler<FrameworkMessage<Echo>> {

    public EchoHandler() {
        super();
    }

    @Override
    public void handle(FrameworkMessage<Echo> request) {
        send(request.reply(frameworkMessage()
                .withBody(new Echo(request.getBody().echo()))
                .build())
        );
    }
}
