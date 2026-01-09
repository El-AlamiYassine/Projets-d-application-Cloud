import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.wrapper.AgentContainer;
import jade.wrapper.AgentController;
import jade.wrapper.StaleProxyException;
import agents.StudentAgent;
import agents.ResourceManagerAgent;

/**
 * Main class to start the JADE platform with multiple StudentAgents
 * Demonstrates concurrency issues and synchronization strategies
 */
public class Main {
    public static void main(String[] args) {
        try {
            System.out.println("Starting JADE platform for Projet 4...");
            System.out.println("Projet 4 – Concurrence et synchronisation");
            System.out.println("Multiple StudentAgent sending RESERVE requests simultaneously");
            System.out.println("Observing conflicts and testing synchronization strategies\n");
            
            // Create the main container
            jade.core.Runtime runtime = jade.core.Runtime.instance();
            Profile profile = new ProfileImpl();
            AgentContainer mainContainer = runtime.createMainContainer(profile);
            
            // Start the ResourceManagerAgent first (to handle requests)
            AgentController rmController = mainContainer.acceptNewAgent(
                "ResourceManager", 
                new ResourceManagerAgent()
            );
            rmController.start();
            
            // Start multiple StudentAgents to demonstrate concurrency
            int numStudents = 5; // Number of student agents to create
            AgentController[] studentControllers = new AgentController[numStudents];
            
            for (int i = 0; i < numStudents; i++) {
                String studentId = "Student_" + (i + 1);
                studentControllers[i] = mainContainer.acceptNewAgent(
                    studentId, 
                    new StudentAgent(studentId)
                );
                studentControllers[i].start();
                
                System.out.println("Started " + studentId);
            }
            
            System.out.println("\nAll agents started successfully!");
            System.out.println("Observing concurrent RESERVE requests and synchronization...");
            System.out.println("ResourceManager implements FIFO queue to handle requests in order");
            System.out.println("Check console output for reservation conflicts and resolution\n");
            
        } catch (StaleProxyException e) {
            System.err.println("Error starting agents: " + e.getMessage());
            e.printStackTrace();
        }
    }
}