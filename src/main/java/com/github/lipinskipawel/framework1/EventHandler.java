package com.github.lipinskipawel.framework1;

import com.github.lipinskipawel.base.BaseWorkload;
import com.github.lipinskipawel.base.CustomRequest;
import com.github.lipinskipawel.base.EventType;

/**
 * Every class that wishes to handle maelstrom workload must extend this class.
 *
 * @param <W> type of workload
 */
public abstract class EventHandler<W extends BaseWorkload> {

    /**
     * Clients will receive maelstrom events through this method.
     *
     * @param event from maelstrom cluster
     */
    public abstract void handle(Event<W> event);

    /**
     * Replies to original event send by maelstrom cluster from {@link EventHandler#handle(Event)}. This method is
     * mostly used to send events that are known to maelstrom cluster. Those events can be InitOk, EchOk and similar.
     *
     * @param originalEvent event to reply to
     * @param responseBody  response payload
     * @param <B>           type of response
     */
    public <B extends EventType> void replyAndSend(Event<W> originalEvent, B responseBody) {
        System.out.println(parse(originalEvent.reply(responseBody)));
    }

    /**
     * Sends an event plain event to maelstrom cluster.
     *
     * @param event event that must be sent
     * @param <C>   custom event type defined by the client
     */
    public <C extends CustomRequest> void send(Event<C> event) {
        System.out.println(parse(event));
    }

    private String parse(Event<?> event) {
        return Server.writeRequest(event);
    }
}
