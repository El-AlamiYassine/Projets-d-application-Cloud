@echo off
echo Compilation du projet de tolérance aux pannes...
javac -cp ".;lib/jade.jar" *.java

if %errorlevel% neq 0 (
    echo Erreur de compilation
    pause
    exit /b %errorlevel%
)

echo.
echo Lancement du projet de tolérance aux pannes...
java -cp ".;lib/jade.jar" Main

pause
