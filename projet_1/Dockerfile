# 1. Image Java officielle (Temurin)
FROM eclipse-temurin:21-jdk

# 2. Dossier de travail
WORKDIR /app

# 3. Copier le fichier Java
COPY HelloWorld.java .

# 4. Compiler le programme
RUN javac HelloWorld.java

# 5. Commande d’exécution
CMD ["java", "HelloWorld"]
