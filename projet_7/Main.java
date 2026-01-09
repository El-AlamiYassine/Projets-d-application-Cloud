import jade.core.Runtime;
import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.wrapper.AgentContainer;
import jade.wrapper.AgentController;
import jade.util.ExtendedProperties;
import jade.util.leap.Properties;

/**
 * Main - Point d'entrée du système de tolérance aux pannes
 * Lance la plateforme JADE avec un agent de surveillance et des agents
 * principaux
 */
public class Main {
  public static void main(String[] args) {
    try {
      System.out.println("=================================================");
      System.out.println("  SYSTÈME DE TOLÉRANCE AUX PANNES - JADE");
      System.out.println("=================================================\n");
      System.out.println("Démarrage de la plateforme JADE...\n");

      // Créer l'environnement d'exécution JADE
      Runtime runtime = Runtime.instance();

      // Configurer le profil pour le conteneur principal
      Properties properties = new ExtendedProperties();
      properties.setProperty(Profile.GUI, "true"); // Activer l'interface graphique
      Profile profile = new ProfileImpl(properties);

      // Créer le conteneur principal
      AgentContainer container = runtime.createMainContainer(profile);

      System.out.println("Création des agents...\n");

      // Créer un agent de surveillance
      AgentController monitorController = container.createNewAgent(
          "MonitorAgent",
          "MonitorAgent",
          new Object[] {});
      monitorController.start();
      System.out.println("✓ MonitorAgent créé");

      // Créer des agents principaux
      for (int i = 0; i < 3; i++) {
        AgentController mainController = container.createNewAgent(
            "MainAgent" + i,
            "MainAgent",
            new Object[] { i });
        mainController.start();
        System.out.println("✓ MainAgent" + i + " créé");
      }

      // Créer des agents de secours
      for (int i = 0; i < 3; i++) {
        AgentController backupController = container.createNewAgent(
            "BackupAgent" + i,
            "BackupAgent",
            new Object[] { i });
        backupController.start();
        System.out.println("✓ BackupAgent" + i + " créé");
      }

      System.out.println("\n=================================================");
      System.out.println("  SYSTÈME DÉMARRÉ AVEC SUCCÈS");
      System.out.println("=================================================");
      System.out.println("\nConfiguration:");
      System.out.println("  - 1 agent de surveillance (MonitorAgent)");
      System.out.println("  - 3 agents principaux (MainAgent0-2)");
      System.out.println("  - 3 agents de secours (BackupAgent0-2)");
      System.out.println("\nScénario de test:");
      System.out.println("  1. MonitorAgent envoie des PING toutes les 5 secondes");
      System.out.println("  2. Après 15 secondes, MainAgent0 simulera une panne");
      System.out.println("  3. MonitorAgent détectera la panne (3 PING manqués)");
      System.out.println("  4. BackupAgent0 sera activé automatiquement");
      System.out.println("\nConsultez la console pour voir l'exécution...\n");

    } catch (Exception e) {
      System.err.println("ERREUR lors du démarrage de la plateforme JADE:");
      System.err.println(e.getMessage());
      e.printStackTrace();
    }
  }
}