package utils;

/**
 * Lamport Clock implementation for logical time ordering
 * Used to determine causal relationships between events in distributed systems
 */
public class LamportClock {
    private int clock = 0; // The logical clock value
    
    /**
     * Constructor initializes the clock to 0
     */
    public LamportClock() {
        this.clock = 0;
    }
    
    /**
     * Increment the clock value (called before sending a message)
     */
    public synchronized void increment() {
        clock++;
    }
    
    /**
     * Update the clock with received message's clock value
     * Called when receiving a message - takes max of local clock and received clock + 1
     */
    public synchronized void update(int receivedClock) {
        clock = Math.max(clock, receivedClock) + 1;
    }
    
    /**
     * Get the current clock value
     */
    public synchronized int getValue() {
        return clock;
    }
    
    /**
     * Set the clock to a specific value (for testing purposes)
     */
    public synchronized void setValue(int value) {
        this.clock = value;
    }
    
    /**
     * Compare this clock with another clock value
     * @param otherClock The other clock value to compare with
     * @return -1 if this < other, 0 if equal, 1 if this > other
     */
    public int compare(int otherClock) {
        if (this.clock < otherClock) {
            return -1;
        } else if (this.clock > otherClock) {
            return 1;
        } else {
            return 0;
        }
    }
    
    @Override
    public String toString() {
        return "LamportClock{value=" + clock + "}";
    }
}