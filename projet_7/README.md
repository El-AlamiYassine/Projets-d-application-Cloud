# Projet 7 - Système de Tolérance aux Pannes avec JADE

## Description
Ce projet implémente un système de tolérance aux pannes basé sur des agents intelligents utilisant le framework JADE (Java Agent Development Framework). Le système comprend un agent de surveillance qui détecte les pannes d'agents et active automatiquement des agents de secours pour maintenir la disponibilité du système.

## Architecture du Système
Le projet est composé de plusieurs types d'agents :

- **MonitorAgent** : Agent de surveillance qui envoie des messages heartbeat (PING) aux agents surveillés et détecte les pannes
- **MainAgent** : Agents principaux qui répondent aux messages heartbeat (PONG)
- **BackupAgent** : Agents de secours qui restent en veille et sont activés automatiquement en cas de panne d'un agent principal

## Fonctionnalités
- Surveillance continue des agents principaux via des messages heartbeat toutes les 5 secondes
- Détection automatique des pannes après 3 messages PING manqués
- Activation immédiate des agents de secours pour remplacer les agents en panne
- Simulation de panne intégrée pour tester la tolérance aux pannes (MainAgent0 simule une panne après 15 secondes)

## Composition du projet
- `Main.java` : Point d'entrée du système, lance la plateforme JADE et crée tous les agents
- `MonitorAgent.java` : Agent de surveillance qui gère la détection des pannes
- `MainAgent.java` : Agent principal qui répond aux messages heartbeat et simule une panne
- `BackupAgent.java` : Agent de secours activé en cas de panne d'un agent principal
- `run.bat` : Script Windows pour compiler et exécuter le projet
- `lib/jade.jar` : Bibliothèque JADE nécessaire à l'exécution
- `APDescription.txt` : Fichier de configuration de la plateforme JADE
- `MTPs-Main-Container.txt` : Fichier de configuration des protocoles de communication

## Prérequis
- Java JDK 8 ou supérieur
- Le framework JADE (fourni dans lib/jade.jar)

## Comment exécuter le projet
Sur Windows :
```cmd
run.bat
```

Ou manuellement :
```cmd
# Compiler le projet
javac -cp ".;lib/jade.jar" *.java

# Exécuter le projet
java -cp ".;lib/jade.jar" Main
```

## Scénario de test
1. Le MonitorAgent envoie des messages PING toutes les 5 secondes
2. Après 15 secondes, MainAgent0 simule une panne (arrêt de la réponse aux PING)
3. Le MonitorAgent détecte la panne après 3 PING manqués
4. Le BackupAgent0 est activé automatiquement pour remplacer MainAgent0
5. Le système continue de fonctionner normalement avec le BackupAgent0 actif

## Structure de l'architecture de tolérance aux pannes
- 1 agent de surveillance (MonitorAgent)
- 3 agents principaux (MainAgent0-2)
- 3 agents de secours (BackupAgent0-2)
- Surveillance par heartbeat avec seuil configurable de détection de panne
- Activation automatique des agents de remplacement