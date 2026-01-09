package agents;

import jade.core.Agent;
import jade.core.AID;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import messages.ReserveMessage;

import java.util.Random;

/**
 * StudentAgent class - sends RESERVE messages to try to book resources
 * Demonstrates concurrency issues when multiple agents send requests simultaneously
 */
public class StudentAgent extends Agent {
    private String studentId;
    private Random random;
    
    // Constructor
    public StudentAgent(String studentId) {
        this.studentId = studentId;
        this.random = new Random();
    }
    
    @Override
    protected void setup() {
        // Initialize the agent
        System.out.println("StudentAgent " + studentId + " is ready.");
        
        // Add behaviour to send RESERVE messages periodically
        addBehaviour(new CyclicBehaviour(this) {
            @Override
            public void action() {
                // Wait a random time before sending next message
                try {
                    Thread.sleep(random.nextInt(2000) + 1000); // 1-3 seconds
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                
                // Send a RESERVE message
                sendReserveMessage();
            }
        });
    }
    
    /**
     * Method to send a RESERVE message
     */
    private void sendReserveMessage() {
        // Create a reservation request
        String resourceId = "Resource_" + (random.nextInt(3) + 1); // 3 different resources
        String reservationId = studentId + "_RES_" + System.currentTimeMillis();
        
        // Create the reserve message
        ReserveMessage reserveMsg = new ReserveMessage(reservationId, resourceId, studentId);
        
        // Create ACL message
        ACLMessage msg = reserveMsg.getACLMessage();
        
        // Set receiver (in a real system this would be the resource manager agent)
        msg.addReceiver(new AID("ResourceManager", AID.ISLOCALNAME));
        
        // Send the message
        send(msg);
        
        System.out.println("StudentAgent " + studentId + " sent RESERVE request for " + 
                          resourceId);
    }
    
    @Override
    protected void takeDown() {
        System.out.println("StudentAgent " + studentId + " is terminating.");
    }
    
    // Getter for student ID
    public String getStudentId() {
        return studentId;
    }
}