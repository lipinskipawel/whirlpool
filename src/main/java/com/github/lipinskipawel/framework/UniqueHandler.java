package com.github.lipinskipawel.framework;

import com.github.lipinskipawel.framework.BuiltInBodies.Unique;

import java.util.List;

import static com.github.lipinskipawel.framework.FrameworkMessage.Builder.frameworkMessage;
import static java.util.Optional.of;
import static java.util.UUID.randomUUID;

/**
 * Run unique-ids maelstrom workload
 * ./maelstrom test -w unique-ids --bin whirlpool.sh --time-limit 30 --rate 1000 --node-count 3 --availability total --nemesis partition
 */
public final class UniqueHandler extends RequestHandler<FrameworkMessage<Unique>> {

    @Override
    public void handle(FrameworkMessage<Unique> message) {
        send(message.reply(frameworkMessage()
                .withBody(new Unique(of(randomUUID())))
                .build()));
    }

    @Override
    public void init(String nodeId, List<String> nodesIds) {
    }

    @Override
    public void quit() {
    }
}
