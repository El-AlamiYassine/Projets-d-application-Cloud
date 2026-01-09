@echo off
REM Script pour lancer le projet JADE
REM Assurez-vous d'avoir téléchargé jade.jar et de l'avoir placé dans le répertoire lib

if not exist "lib\jade.jar" (
    echo ERREUR: jade.jar n'est pas trouvé dans le répertoire lib
    echo Téléchargez JADE depuis http://jade.tilab.com/ et placez jade.jar dans le répertoire lib
    exit /b 1
)

echo Compilation du projet...
javac -cp "lib\jade.jar" -d bin src\projet3\*.java

if errorlevel 1 (
    echo Erreur de compilation
    exit /b 1
)

echo Lancement du projet...
java -cp "bin;lib\jade.jar" jade.Boot -gui -host localhost -port 1200 -agents "resource:projet3.ResourceAgent;client:projet3.ClientAgent"

pause