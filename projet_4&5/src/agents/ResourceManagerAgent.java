package agents;

import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import messages.ReserveMessage;
import utils.LamportClock;

import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * ResourceManagerAgent - handles RESERVE requests with queue-based synchronization
 * Implements FIFO ordering to handle concurrent requests properly
 */
public class ResourceManagerAgent extends Agent {
    // Queue to store incoming reservation requests (FIFO)
    private Queue<ReservationRequest> requestQueue;
    // Set to track already reserved resources
    private Set<String> reservedResources;
    // Map to track which student reserved which resource
    private Map<String, String> resourceStudentMap;
    // Lamport clock for logical time ordering
    private LamportClock lamportClock;
    // Processing flag to handle one request at a time
    private boolean isProcessing;
    
    @Override
    protected void setup() {
        // Initialize data structures
        this.requestQueue = new ConcurrentLinkedQueue<>();
        this.reservedResources = new HashSet<>();
        this.resourceStudentMap = new HashMap<>();
        this.lamportClock = new LamportClock();
        this.isProcessing = false;
        
        System.out.println("ResourceManagerAgent is ready to handle RESERVE requests.");
        
        // Add behaviour to handle incoming messages
        addBehaviour(new CyclicBehaviour(this) {
            @Override
            public void action() {
                // Receive messages
                ACLMessage msg = receive(MessageTemplate.MatchPerformative(ACLMessage.REQUEST));
                
                if (msg != null) {
                    // Update Lamport clock with received message clock
                    String clockStr = msg.getUserDefinedParameter("clock");
                    if (clockStr != null) {
                        int receivedClock = Integer.parseInt(clockStr);
                        lamportClock.update(receivedClock);
                    } else {
                        lamportClock.increment();
                    }
                    
                    // Parse the reservation message
                    ReserveMessage reserveMsg = ReserveMessage.fromACLMessage(msg);
                    if (reserveMsg != null) {
                        // Create a request with timestamp for FIFO ordering
                        ReservationRequest request = new ReservationRequest(
                            reserveMsg, 
                            msg.getSender(), 
                            lamportClock.getValue()
                        );
                        
                        // Add to queue for FIFO processing
                        requestQueue.add(request);
                        System.out.println("ResourceManager: Added request to queue - " + reserveMsg.getResourceId() + 
                                         " for student " + reserveMsg.getStudentId() + 
                                         " with clock=" + lamportClock.getValue());
                    }
                }
                
                // Process requests in FIFO order
                processNextRequest();
                
                // Block for a short time to allow other agents to act
                block(100);
            }
        });
    }
    
    /**
     * Process the next request in the queue (FIFO order)
     */
    private void processNextRequest() {
        if (!isProcessing && !requestQueue.isEmpty()) {
            ReservationRequest request = requestQueue.poll();
            
            if (request != null) {
                isProcessing = true;
                
                // Process the reservation request
                String resourceId = request.getReserveMessage().getResourceId();
                String studentId = request.getReserveMessage().getStudentId();
                
                // Check if resource is already reserved
                if (!reservedResources.contains(resourceId)) {
                    // Reserve the resource
                    reservedResources.add(resourceId);
                    resourceStudentMap.put(resourceId, studentId);
                    
                    // Send confirmation to student
                    ACLMessage confirmMsg = new ACLMessage(ACLMessage.INFORM);
                    confirmMsg.addReceiver(request.getSender());
                    confirmMsg.setContent("CONFIRM|RESERVED|" + resourceId + "|" + studentId + "|clock=" + lamportClock.getValue());
                    send(confirmMsg);
                    
                    System.out.println("ResourceManager: Resource " + resourceId + 
                                     " successfully reserved for student " + studentId);
                } else {
                    // Resource already reserved, send rejection
                    ACLMessage rejectMsg = new ACLMessage(ACLMessage.FAILURE);
                    rejectMsg.addReceiver(request.getSender());
                    rejectMsg.setContent("REJECT|ALREADY_RESERVED|" + resourceId + "|" + 
                                       resourceStudentMap.get(resourceId) + "|clock=" + lamportClock.getValue());
                    send(rejectMsg);
                    
                    System.out.println("ResourceManager: Resource " + resourceId + 
                                     " already reserved by student " + resourceStudentMap.get(resourceId) + 
                                     ". Rejected request from student " + studentId);
                }
                
                isProcessing = false;
            }
        }
    }
    
    @Override
    protected void takeDown() {
        System.out.println("ResourceManagerAgent is terminating.");
        System.out.println("Final reservations: " + resourceStudentMap);
    }
    
    /**
     * Inner class to represent a reservation request with its metadata
     */
    private class ReservationRequest {
        private ReserveMessage reserveMessage;
        private jade.core.AID sender;
        private int timestamp; // For FIFO ordering
        
        public ReservationRequest(ReserveMessage reserveMessage, jade.core.AID sender, int timestamp) {
            this.reserveMessage = reserveMessage;
            this.sender = sender;
            this.timestamp = timestamp;
        }
        
        public ReserveMessage getReserveMessage() {
            return reserveMessage;
        }
        
        public jade.core.AID getSender() {
            return sender;
        }
        
        public int getTimestamp() {
            return timestamp;
        }
    }
}