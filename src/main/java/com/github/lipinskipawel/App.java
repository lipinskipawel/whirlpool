package com.github.lipinskipawel;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.github.lipinskipawel.framework2.Register;
import com.github.lipinskipawel.framework2.protocol.Echo;
import com.github.lipinskipawel.framework2.protocol.EchoOk;
import com.github.lipinskipawel.framework2.protocol.Init;
import com.github.lipinskipawel.framework2.protocol.InitOk;
import com.github.lipinskipawel.protocol.InitBody;
import com.github.lipinskipawel.protocol.Json;
import com.github.lipinskipawel.protocol.Message;
import com.github.lipinskipawel.workload.BroadcastResponder;
import com.github.lipinskipawel.workload.EchoResponder;
import com.github.lipinskipawel.workload.UniqueResponder;

import java.io.InputStream;
import java.util.Scanner;

import static com.github.lipinskipawel.protocol.Message.messageWithInitBody;

/**
 * Run server with logs
 * java -jar lib/maelstrom.jar serve
 */
public class App {
    private static final EchoResponder echoResponder = new EchoResponder();
    private static final UniqueResponder uniqueResponder = new UniqueResponder();
    private static BroadcastResponder broadcastResponder;

    public static void main(String[] args) {
        final var register = new Register();
        Register.configure("init", TypeFactory.defaultInstance().constructFromCanonical(Init.class.getCanonicalName()));
        Register.configure("init_ok", TypeFactory.defaultInstance().constructFromCanonical(InitOk.class.getCanonicalName()));
        Register.configure("echo", TypeFactory.defaultInstance().constructFromCanonical(Echo.class.getCanonicalName()));
        Register.configure("echo_ok", TypeFactory.defaultInstance().constructFromCanonical(EchoOk.class.getCanonicalName()));
        register.loop();
//        FrameworkEntryPoint.register(new BroadcastHandler(), new FrameworkBroadcastDeserializer(), new FrameworkBroadcastSerializer())
//                .start();
//        parse(System.in);
    }

    private static void parse(InputStream inputStream) throws InterruptedException {
        try (var scanner = new Scanner(inputStream)) {
            final var initRequest = scanner.nextLine();
            final var initMessage = Json.toObject(initRequest, new TypeReference<Message<InitBody>>() {
            });
            final var initOk = Json.toJson(messageWithInitBody(initMessage.dst(), initMessage.src(), body -> body
                    .withType("init_ok")
                    .withInReplyTo(1)
            ));
            broadcastResponder = new BroadcastResponder(initMessage.body().nodeId().get(), initMessage.body().nodeIds().get());
            System.out.println(initOk);

            while (scanner.hasNextLine()) {
                final var request = scanner.nextLine();
                broadcastResponder.handle(request);
            }
            broadcastResponder.handle("quit");
        }
    }
}
