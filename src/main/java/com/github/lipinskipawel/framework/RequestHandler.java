package com.github.lipinskipawel.framework;

import static com.github.lipinskipawel.framework.FrameworkJson.toJson;

public abstract class RequestHandler<M extends FrameworkMessage<?>> {

    protected int msgCounter;

    protected RequestHandler() {
        this.msgCounter = 0;
    }

    public abstract void handle(M message);

    @SuppressWarnings("unchecked")
    public void send(M object) {
        final M toSend = (M) object.copy()
                .withMsgId(++msgCounter)
                .build();
        System.out.println(parse(toSend));
    }

    public void debug(M object) {
        System.err.println(parse(object));
    }

    public void debug(String text, M object) {
        System.err.println(text + parse(object));
    }

    private String parse(M object) {
        return toJson(object);
    }
}
