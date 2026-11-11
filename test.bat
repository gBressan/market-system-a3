@echo off
rem Compila o sistema e os testes e executa todos os testes (Windows).
cd /d "%~dp0"
javac -encoding UTF-8 -d out\main -sourcepath src\main\java src\main\java\marketsystem\Main.java || exit /b 1
javac -encoding UTF-8 -d out\test -cp out\main -sourcepath src\test\java src\test\java\marketsystem\testing\TestRunner.java || exit /b 1
java -cp out\main;out\test marketsystem.testing.TestRunner
