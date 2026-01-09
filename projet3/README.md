# Projet 3 - Système Multi-Agent de Réservation de Ressources avec JADE

## Description
Ce projet implémente un système multi-agent simple basé sur le framework JADE (Java Agent Development Framework) pour la gestion de réservation d'une ressource partagée entre plusieurs clients. Le système permet à différents agents clients de demander la réservation d'une ressource unique gérée par un agent de ressource.

## Architecture du Système
Le projet est composé de plusieurs types d'agents :

- **ResourceAgent** : Agent qui gère une ressource partagée et traite les demandes de réservation
- **ClientAgent** : Agent client de base qui envoie une demande de réservation
- **Client1Agent** : Premier agent client spécialisé qui envoie une demande de réservation
- **Client2Agent** : Deuxième agent client spécialisé qui envoie une demande de réservation

## Fonctionnalités
- Gestion d'une ressource partagée avec mécanisme de réservation exclusif
- Détection d'état de disponibilité de la ressource (disponible/occupée)
- Réponse aux demandes de réservation avec acceptation ou refus selon la disponibilité
- Communication entre agents via le protocole FIPA-ACL

## Composition du projet
- `Main.java` : Point d'entrée du système, lance la plateforme JADE et crée les agents
- `ResourceAgent.java` : Agent responsable de la gestion de la ressource partagée
- `ClientAgent.java` : Agent client de base qui envoie une demande de réservation
- `Client1Agent.java` : Premier agent client spécialisé
- `Client2Agent.java` : Deuxième agent client spécialisé
- `lancer_projet.bat` : Script Windows pour compiler et exécuter le projet
- `lib/jade.jar` : Bibliothèque JADE nécessaire à l'exécution
- `APDescription.txt` : Fichier de configuration de la plateforme JADE
- `MTPs-Main-Container.txt` : Fichier de configuration des protocoles de communication
- `src/` : Répertoire contenant les fichiers source Java
- `bin/` : Répertoire de compilation pour les fichiers bytecode

## Prérequis
- Java JDK 8 ou supérieur
- Le framework JADE (fourni dans lib/jade.jar)

## Comment exécuter le projet
Sur Windows :
```cmd
lancer_projet.bat
```

Ou manuellement :
```cmd
# Compiler le projet
javac -cp "lib\jade.jar" -d bin src\projet3\*.java

# Exécuter le projet
java -cp "bin;lib\jade.jar" jade.Boot -gui -host localhost -port 1200 -agents "resource:projet3.ResourceAgent;client:projet3.ClientAgent"
```

## Fonctionnement du système
1. L'agent ResourceAgent démarre et se met en attente de demandes de réservation
2. Les agents clients (ClientAgent, Client1Agent, Client2Agent) envoient des demandes de réservation
3. L'agent ResourceAgent traite les demandes dans l'ordre de réception
4. Si la ressource est disponible, elle est réservée (statut mis à indisponible) et une réponse "ACCETER" est envoyée
5. Si la ressource est occupée, une réponse "REFUSER" est envoyée
6. Le système affiche des messages dans la console pour indiquer l'état des transactions

## Protocole de communication
- Les agents communiquent via des messages ACL (Agent Communication Language)
- Les messages REQUEST sont utilisés pour les demandes de réservation
- Les réponses contiennent soit "ACCETER" (accepté) soit "REFUSER" (refusé)
- Les agents utilisent des identifiants locaux pour la communication intra-conteneur

## Cas d'utilisation typiques
- Systèmes de réservation de ressources partagées
- Allocation dynamique de ressources dans un environnement distribué
- Contrôle d'accès concurrentiel à une ressource critique
- Simulation de systèmes multi-agents simples