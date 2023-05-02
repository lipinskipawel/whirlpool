package com.github.lipinskipawel;

import com.fasterxml.jackson.core.type.TypeReference;
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
    private static final BroadcastResponder broadcastResponder = new BroadcastResponder();

    public static void main(String[] args) {
        parse(System.in);
    }

    private static void parse(InputStream inputStream) {
        try (var scanner = new Scanner(inputStream)) {
            final var initRequest = scanner.nextLine();
            final var initMessage = Json.toObject(initRequest, new TypeReference<Message<InitBody>>() {
            });
            final var initOk = Json.toJson(messageWithInitBody(initMessage.dst(), initMessage.src(), body -> body
                    .withType("init_ok")
                    .withInReplyTo(1)
            ));
            System.out.println(initOk);

            while (scanner.hasNextLine()) {
                final var request = scanner.nextLine();
                System.out.println(broadcastResponder.handle(request));
            }
        }
    }
}
