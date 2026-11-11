#!/usr/bin/env sh
# Compila e executa o sistema (Linux, macOS ou Git Bash no Windows).
set -e
cd "$(dirname "$0")"
javac -encoding UTF-8 -d out/main -sourcepath src/main/java src/main/java/marketsystem/Main.java
java -Dstdout.encoding=UTF-8 -cp out/main marketsystem.Main
