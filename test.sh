#!/usr/bin/env sh
# Compila o sistema e os testes e executa todos os testes (Linux, macOS ou Git Bash no Windows).
set -e
cd "$(dirname "$0")"
SEPARATOR=":"
case "$(uname -s)" in MINGW*|MSYS*|CYGWIN*) SEPARATOR=";" ;; esac
javac -encoding UTF-8 -d out/main -sourcepath src/main/java src/main/java/marketsystem/Main.java
javac -encoding UTF-8 -d out/test -cp out/main -sourcepath src/test/java src/test/java/marketsystem/testing/TestRunner.java
java -Dstdout.encoding=UTF-8 -cp "out/main${SEPARATOR}out/test" marketsystem.testing.TestRunner
