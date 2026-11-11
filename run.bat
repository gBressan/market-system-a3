@echo off
rem Compila e executa o sistema (Windows: cmd, PowerShell ou duplo clique).
cd /d "%~dp0"
javac -encoding UTF-8 -d out\main -sourcepath src\main\java src\main\java\marketsystem\Main.java || exit /b 1
java -cp out\main marketsystem.Main
