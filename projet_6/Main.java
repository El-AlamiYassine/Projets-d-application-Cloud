import jade.core.Runtime;
import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.wrapper.AgentContainer;
import jade.wrapper.AgentController;
import jade.util.ExtendedProperties;
import jade.util.leap.Properties;

/**
 * Main class - Point d'entrée du système de consensus distribué
 * Lance la plateforme JADE avec plusieurs agents CoordinatorAgent
 */
public class Main {
    public static void main(String[] args) {
        try {
            System.out.println("Démarrage de la plateforme JADE pour le projet de consensus distribué...");
            
            // Créer l'environnement d'exécution JADE
            Runtime runtime = Runtime.instance();
            
            // Configurer le profil pour le conteneur principal
            Properties properties = new ExtendedProperties();
            properties.setProperty(Profile.GUI, "false"); // Désactiver l'interface graphique dans Docker
                    
            // Vérifier si on est dans un environnement Docker
            String isMain = System.getenv("JADE_MAIN");
            if (isMain != null && isMain.equals("true")) {
                // Conteneur principal - conserver la configuration originale
                properties.setProperty(Profile.MAIN, "true");
            } else {
                // Conteneur simple - peut être utilisé dans un environnement distribué
                properties.setProperty(Profile.MAIN, "false");
            }
                    
            Profile profile = new ProfileImpl(properties);
            
            // Créer le conteneur principal
            AgentContainer container = runtime.createMainContainer(profile);
            
            // Créer plusieurs agents CoordinatorAgent avec des IDs différents
            String[] agentNames = {"Agent0", "Agent1", "Agent2", "Agent3", "Agent4"};
            int[] agentIds = {10, 30, 20, 50, 40}; // Chaque agent a un ID unique
            
            for (int i = 0; i < agentNames.length; i++) {
                // Créer un agent avec son ID comme argument
                Object[] arguments = new Object[]{agentIds[i]};
                AgentController agentController = container.createNewAgent(
                    agentNames[i], 
                    CoordinatorAgent.class.getName(), 
                    arguments
                );
                agentController.start();
                System.out.println("Agent " + agentNames[i] + " (ID: " + agentIds[i] + ") créé");
            }
            
            // Créer un agent de backup principal (optionnel)
            try {
                Object[] backupArgs = new Object[]{100};
                AgentController backupController = container.createNewAgent(
                    "BackupAgentMain", 
                    BackupAgent.class.getName(), 
                    backupArgs
                );
                backupController.start();
                System.out.println("Backup Agent principal créé (ID: 100)");
            } catch (Exception e) {
                System.err.println("Erreur lors de la création de l'agent de backup principal: " + e.getMessage());
            }
            
            System.out.println("Tous les agents sont créés. L'élection du leader va commencer...");
            System.out.println("Algorithme Bully: L'agent avec le plus grand ID deviendra le leader");
            System.out.println("Dans cet exemple: Agent avec ID 50 devrait devenir le leader");
            
        } catch (Exception e) {
            System.err.println("Erreur lors du démarrage de la plateforme JADE: " + e.getMessage());
            e.printStackTrace();
        }
    }
}