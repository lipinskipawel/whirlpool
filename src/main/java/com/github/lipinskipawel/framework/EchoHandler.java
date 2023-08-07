package com.github.lipinskipawel.framework;

import java.util.List;

import static com.github.lipinskipawel.framework.BuiltInBodies.Echo;
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
    public void handle(FrameworkMessage<Echo> message) {
        send(message.reply(frameworkMessage()
                .withBody(new Echo(message.getBody().echo()))
                .build())
        );
    }

    @Override
    public void init(String nodeId, List<String> nodesIds) {
    }

    @Override
    public void quit() {
    }
}
