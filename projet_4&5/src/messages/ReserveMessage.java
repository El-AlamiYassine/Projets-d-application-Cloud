package messages;

import jade.core.AID;
import jade.lang.acl.ACLMessage;

/**
 * Message class for RESERVE operations
 * Used to handle reservation requests between agents
 */
public class ReserveMessage {
    private String reservationId;
    private String resourceId;
    private String studentId;
    private long timestamp;
    
    // Constructor
    public ReserveMessage(String reservationId, String resourceId, String studentId) {
        this.reservationId = reservationId;
        this.resourceId = resourceId;
        this.studentId = studentId;
        this.timestamp = System.currentTimeMillis();
    }
    
    // Get ACLMessage for JADE communication
    public ACLMessage getACLMessage() {
        ACLMessage msg = new ACLMessage(ACLMessage.REQUEST);
        msg.setContent("RESERVE|" + reservationId + "|" + resourceId + "|" + studentId);
        return msg;
    }
    
    // Create ReserveMessage from ACLMessage
    public static ReserveMessage fromACLMessage(ACLMessage aclMsg) {
        String content = aclMsg.getContent();
        String[] parts = content.split("\\|");
        if (parts.length >= 4 && parts[0].equals("RESERVE")) {
            return new ReserveMessage(parts[1], parts[2], parts[3]);
        }
        return null;
    }
    
    // Getters
    public String getReservationId() {
        return reservationId;
    }
    
    public String getResourceId() {
        return resourceId;
    }
    
    public String getStudentId() {
        return studentId;
    }
    
    public long getTimestamp() {
        return timestamp;
    }
    
    @Override
    public String toString() {
        return "ReserveMessage{" +
                "reservationId='" + reservationId + '\'' +
                ", resourceId='" + resourceId + '\'' +
                ", studentId='" + studentId + '\'' +
                ", timestamp=" + timestamp +
                '}';
    }
}