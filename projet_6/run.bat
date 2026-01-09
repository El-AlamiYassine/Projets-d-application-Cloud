@echo off
echo Compilation du projet...
javac -cp ".;lib/jade.jar" *.java

if %errorlevel% neq 0 (
    echo Erreur de compilation
    pause
    exit /b %errorlevel%
)

echo.
echo Lancement du projet de consensus distribue...
java -cp ".;lib/jade.jar" Main

pause