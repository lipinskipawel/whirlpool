#!/bin/bash

SCRIPT_DIR=$( cd -- "$( dirname -- "${BASH_SOURCE[0]}" )" &> /dev/null && pwd )
JAR_EXECUTABLE=$SCRIPT_DIR/build/libs/whirlpool.jar

java -Djava.awt.headless=true -jar $JAR_EXECUTABLE
