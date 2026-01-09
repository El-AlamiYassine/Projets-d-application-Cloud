# Projet 8 - Application Hello World Node.js

## Description
Ce projet est une application simple "Hello World" développée avec Node.js. Il s'agit d'un serveur HTTP basique qui affiche une page web élégante avec un message de bienvenue et des informations sur le serveur.

## Fonctionnalités
- Serveur HTTP utilisant le module natif `http` de Node.js
- Page d'accueil HTML stylisée avec CSS intégré
- Affichage du numéro de port sur lequel le serveur est en cours d'exécution
- Gestion des erreurs 404 pour les routes non trouvées
- Interface web responsive avec design moderne

## Composition du projet
- `server.js` : Fichier principal contenant le serveur HTTP et la logique de routage
- `package.json` : Fichier de configuration du projet Node.js avec dépendances et scripts
- `Dockerfile` : Instructions pour créer une image Docker de l'application
- `.dockerignore` : Fichier spécifiant quels fichiers ignorer lors de la construction de l'image Docker
- `package-lock.json` : Fichier de verrouillage des versions des dépendances

## Configuration
- Port d'écoute : 3000
- Hôte d'écoute : 0.0.0.0 (accessible depuis n'importe quelle adresse IP)
- Type de module : CommonJS

## Comment exécuter le projet

### Localement
1. Assurez-vous d'avoir Node.js installé sur votre système
2. Installez les dépendances (s'il y en a) :
   ```bash
   npm install
   ```
3. Lancez l'application :
   ```bash
   npm start
   ```
   ou
   ```bash
   node server.js
   ```

4. Ouvrez votre navigateur et allez à l'adresse : http://localhost:3000

### Avec Docker
1. Assurez-vous d'avoir Docker installé sur votre système
2. Depuis le répertoire du projet, construisez l'image Docker :
   ```bash
   docker build -t hello-world-node .
   ```
3. Exécutez le conteneur :
   ```bash
   docker run -p 3000:3000 hello-world-node
   ```
4. Ouvrez votre navigateur et allez à l'adresse : http://localhost:3000

## Structure du code
- Le serveur écoute sur toutes les interfaces réseau (0.0.0.0) sur le port 3000
- Gère deux routes : la racine (`/` ou `/index.html`) et les autres routes (erreur 404)
- La page d'accueil affiche "Hello World!" avec des informations sur le serveur
- Les autres requêtes renvoient une page d'erreur 404

## Scripts npm disponibles
- `npm start` : Lance l'application Node.js

## Design de la page web
La page d'accueil dispose d'un design moderne avec :
- Un dégradé de fond élégant
- Un conteneur centré verticalement et horizontalement
- Des couleurs harmonieuses et lisibles
- Une mise en page responsive qui s'adapte aux différentes tailles d'écran
- Une ombre portée pour un effet de profondeur
- Une police Arial claire et lisible