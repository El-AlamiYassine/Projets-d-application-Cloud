import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;

/**
 * BackupAgent - A backup agent that activates when the leader fails
 * This agent waits for activation commands from the CoordinatorAgent
 */
public class BackupAgent extends Agent {
    private int backupId;
    private boolean isActive = false;
    
    @Override
    protected void setup() {
        Object[] args = getArguments();
        if (args != null && args.length > 0) {
            backupId = Integer.parseInt(args[0].toString());
        } else {
            backupId = (int) (Math.random() * 100);
        }
        
        System.out.println("Backup Agent " + backupId + " démarré");
        
        // Add behavior to handle messages
        addBehaviour(new BackupMessageHandler());
    }
    
    /**
     * Behavior to handle incoming messages
     */
    private class BackupMessageHandler extends CyclicBehaviour {
        @Override
        public void action() {
            ACLMessage msg = receive();
            if (msg != null) {
                String content = msg.getContent();
                
                if (content.equals("ACTIVATE_BACKUP")) {
                    activateBackup();
                } else if (content.startsWith("DEACTIVATE_BACKUP")) {
                    deactivateBackup();
                } else if (content.startsWith("PING")) {
                    // Respond to heartbeat ping
                    ACLMessage response = new ACLMessage(ACLMessage.INFORM);
                    response.addReceiver(msg.getSender());
                    response.setContent("PONG:" + backupId);
                    send(response);
                }
                
                System.out.println("Backup Agent " + backupId + " received message: " + content);
            } else {
                block();
            }
        }
    }
    
    /**
     * Activate the backup agent as a substitute for failed leader
     */
    private void activateBackup() {
        isActive = true;
        System.out.println("Backup Agent " + backupId + " ACTIVATED - Ready to take over!");
        
        // Perform backup activation tasks
        // In a real system, this might involve taking over leader responsibilities
    }
    
    /**
     * Deactivate the backup agent
     */
    private void deactivateBackup() {
        isActive = false;
        System.out.println("Backup Agent " + backupId + " deactivated");
    }
    
    @Override
    protected void takeDown() {
        System.out.println("Backup Agent " + backupId + " s'arrête");
    }
    
    public boolean isActive() {
        return isActive;
    }
}