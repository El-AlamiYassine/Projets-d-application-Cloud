# Projet 4 – Concurrence et synchronisation

## Description
This project demonstrates concurrency and synchronization issues in a multi-agent system using JADE (Java Agent Development Framework). It implements:

1. Multiple StudentAgent sending RESERVE messages simultaneously
2. Observation of conflicts when multiple agents try to reserve the same resource
3. Implementation of a queue-based synchronization strategy with FIFO order
4. Lamport clock implementation for logical time ordering

## Project Structure
- `src/agents/` - Contains agent implementations
  - `StudentAgent.java` - Agents that send RESERVE requests
  - `ResourceManagerAgent.java` - Agent that manages resource reservations with FIFO queue
- `src/messages/` - Contains message implementations
  - `ReserveMessage.java` - Message class for RESERVE operations
- `src/utils/` - Utility classes
  - `LamportClock.java` - Implementation of Lamport logical clock
- `Main.java` - Entry point to start the multi-agent system

## How to Run
1. Make sure you have the JADE library in the `lib/` directory (jade.jar)
2. Compile all Java files:
   ```bash
   javac -cp ".;lib/jade.jar" Main.java src/agents/*.java src/messages/*.java src/utils/*.java
   ```
3. Run the application:
   ```bash
   java -cp ".;lib/jade.jar" Main
   ```

## Features Demonstrated
- **Concurrency Issues**: Multiple StudentAgent sending RESERVE requests simultaneously
- **Conflict Resolution**: ResourceManagerAgent handles concurrent requests using a FIFO queue
- **Synchronization**: Proper ordering of requests to prevent race conditions
- **Lamport Clocks**: Logical time ordering for distributed system events

## Expected Output
- Multiple StudentAgent instances sending RESERVE requests for resources
- ResourceManagerAgent processing requests in FIFO order
- Prevention of resource conflicts through queue-based synchronization
- Lamport clock values showing logical time ordering of events