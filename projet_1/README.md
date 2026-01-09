# Projet 1 - Hello World Docker et Java

## Description
Ce projet est une application simple "Hello World" qui démontre l'utilisation de Docker avec Java. Il contient un programme Java basique qui affiche un message de bienvenue et un fichier Dockerfile pour le conteneuriser.

## Composition du projet
- `HelloWorld.java` : Programme Java simple qui affiche "Hello World depuis Docker et Java !"
- `Dockerfile` : Instructions pour construire une image Docker contenant l'application Java

## Fonctionnement
Le programme est constitué d'une classe Java nommée `HelloWorld` avec une méthode `main()` qui affiche un message sur la console.

Le Dockerfile utilise l'image officielle Eclipse Temurin Java 21 JDK comme base, définit un répertoire de travail, copie le fichier Java dans l'image, le compile et configure la commande d'exécution.

## Étapes du Dockerfile
1. Utilise l'image `eclipse-temurin:21-jdk` comme base
2. Définit `/app` comme répertoire de travail
3. Copie le fichier `HelloWorld.java` dans le conteneur
4. Compile le programme Java avec `javac`
5. Exécute le programme avec `java HelloWorld`

## Pour exécuter le projet
Pour construire et exécuter ce projet avec Docker :
```bash
# Construire l'image Docker
docker build -t hello-world-java .

# Exécuter le conteneur
docker run hello-world-java
```

Cela produira la sortie : "Hello World depuis Docker et Java !"