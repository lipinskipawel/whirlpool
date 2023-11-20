# Whirlpool

Implementation of distributed systems challenges from [fly.io]. This project uses [maelstrom-java] for protocol
implementation and event handling.

[fly.io]: https://fly.io/dist-sys/

[maelstrom-java]: (https://github.com/lipinskipawel/maelstrom-java)

Steps to follow:

- download [maelstrom](https://github.com/jepsen-io/maelstrom)
- run `java -jar lib/maelstrom.jar serve`
- build project `./gradlew clean build`
- run any workload like [broadcast](src/main/java/com/github/lipinskipawel/whirlpool/workload/broadcast/BroadcastHandler.java)

## License

This project is [MIT] licensed.

[MIT]: LICENSE
