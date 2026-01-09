@echo off
echo Compiling Projet 4 - Concurrence et synchronisation...
javac -cp ".;lib/jade.jar" Main.java src/agents/*.java src/messages/*.java src/utils/*.java
if %errorlevel% == 0 (
    echo Compilation successful!
    echo Running the application...
    java -cp ".;lib/jade.jar" Main
) else (
    echo Compilation failed!
    pause
)