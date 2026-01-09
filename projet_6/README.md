# JADE Distributed System with Fault Tolerance

This project implements a distributed system using JADE (Java Agent Development Framework) with fault tolerance capabilities including heartbeat monitoring and backup agent activation.

## Features

- **Distributed Leader Election**: Implements the Bully algorithm for leader election
- **Fault Tolerance**: Heartbeat-based failure detection system
- **Backup Agent Activation**: Automatic backup agent activation when failures are detected
- **Docker Containerization**: Scalable containerized deployment
- **Scalability**: Support for scaling multiple agent instances

## Architecture

- CoordinatorAgent: Main agents that participate in leader election
- BackupAgent: Backup agents that activate when failures are detected
- Heartbeat System: PING/PONG mechanism for failure detection

## Docker Setup

### Prerequisites

- Docker Desktop installed and running
- JDK 17+ (for local development)

### Building and Running

#### Using Docker Compose (Recommended)

1. Build and start the services:
```bash
docker-compose up --build
```

2. To scale resource agents:
```bash
docker-compose up --build --scale resource-agent=3
```

3. To scale coordinator agents:
```bash
docker-compose up --build --scale coordinator-agent=2
```

#### Building Individual Docker Image

1. Build the Docker image:
```bash
docker build -t jade-app .
```

2. Run the container:
```bash
docker run -p 1099:1099 -p 7778:7778 -e JADE_MAIN=true jade-app
```

### Environment Variables

- `JADE_MAIN=true`: Run as main container (required for the main JADE container)
- `JADE_MAIN=false`: Run as peripheral container

## Service Structure

- **jade-main**: Main JADE container that coordinates the system
- **coordinator-agent**: Coordinator agents that participate in leader election
- **resource-agent**: Resource agents that can be scaled as needed

## Fault Tolerance Mechanism

1. **Heartbeat Monitoring**: Agents periodically send PING messages to each other
2. **Failure Detection**: If no heartbeat is received within a timeout period, the agent is considered failed
3. **Backup Activation**: When a failure is detected, backup agents are automatically activated
4. **Leader Recovery**: If the leader fails, a new election process is initiated

## Development

To run locally without Docker:
```bash
javac -cp "lib/*;." *.java
java -cp "lib/*;." Main
```

## Ports

- Port 1099: JADE main container port
- Port 7778: JADE MTP (Message Transport Protocol) port

## Logs

Application logs are stored in the `./logs` directory when using Docker Compose.