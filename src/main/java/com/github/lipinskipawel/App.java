package com.github.lipinskipawel;

import com.fasterxml.jackson.core.type.TypeReference;
import com.github.lipinskipawel.echo.Responder;
import com.github.lipinskipawel.protocol.EchoBody;
import com.github.lipinskipawel.protocol.InitBody;
import com.github.lipinskipawel.protocol.Json;
import com.github.lipinskipawel.protocol.Message;

import java.io.InputStream;
import java.util.Scanner;

import static com.github.lipinskipawel.protocol.Message.messageWithInitBody;

/**
 * Run echo maelstrom test
 * ./maelstrom test -w echo --bin whirlpool.sh --node-count 1 --time-limit 10 --log-stderr
 * <p>
 * Run server with logs
 * java -jar lib/maelstrom.jar serve
 * <p>
 * whirlpool.sh looks like that
 * #!/bin/bash
 * java -jar $PATH_TO_PROJECT/build/libs/whirlpool.jar
 */
public class App {
    private static boolean isInitMessage = true;
    private static Responder responder;

    public static void main(String[] args) {
        parse(System.in);
    }

    private static void parse(InputStream inputStream) {
        try (var scanner = new Scanner(inputStream)) {
            while (scanner.hasNextLine()) {

                final var read = scanner.nextLine();
                if (isInitMessage) {
                    final var initMessage = Json.toObject(read, new TypeReference<Message<InitBody>>() {
                    });
                    final var initOk = Json.toJson(messageWithInitBody(initMessage.dst(), initMessage.src(), body -> body
                            .withType("init_ok")
                            .withInReplyTo(1)
                    ));
                    responder = new Responder(initMessage.body().nodeId().get(), initMessage.body().nodeIds().get());
                    isInitMessage = false;
                    System.out.println(initOk);
                    continue;
                }

                final var message = Json.toObject(read, new TypeReference<Message<EchoBody>>() {
                });
                System.out.println(responder.handle(message));
            }
        }
    }
}
