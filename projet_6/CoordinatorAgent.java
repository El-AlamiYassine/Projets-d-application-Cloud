import jade.core.Agent;
import jade.core.AID;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.TickerBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * CoordinatorAgent - Implements the Bully algorithm for distributed leader election
 * Each agent has an ID and communicates to elect a leader (highest ID)
 */
public class CoordinatorAgent extends Agent {
    private int agentId;                    // ID unique de l'agent
    private int currentLeaderId = -1;       // ID du leader actuel
    private List<Integer> knownAgents;      // Liste des IDs des agents connus
    private boolean isLeader = false;       // Indique si cet agent est le leader
    private Map<Integer, Long> lastHeartbeat; // Dernier heartbeat reçu de chaque agent
    private List<AID> allAgents;             // Liste de tous les agents connus
    private AID backupAgentAID;              // Référence à l'agent de backup
    
    // Initialisation de l'agent avec un ID
    @Override
    protected void setup() {
        Object[] args = getArguments();
        if (args != null && args.length > 0) {
            agentId = Integer.parseInt(args[0].toString());
        } else {
            // Si pas d'ID fourni, on en génère un aléatoire
            agentId = (int) (Math.random() * 100);
        }
        
        knownAgents = new ArrayList<>();
        knownAgents.add(agentId); // On s'ajoute à la liste
        lastHeartbeat = new HashMap<>();
        allAgents = new ArrayList<>();
        
        System.out.println("Agent " + agentId + " démarré");
        
        // Démarrer la découverte des agents
        discoverAgents();
        
        // Ajouter le comportement de communication
        addBehaviour(new MessageHandler());
        
        // Ajouter le comportement de heartbeat
        addBehaviour(new HeartbeatBehaviour(this, 5000)); // Envoyer heartbeat toutes les 5 secondes
        
        // Ajouter le comportement de surveillance des pannes
        addBehaviour(new FailureDetectionBehaviour(this, 8000)); // Vérifier toutes les 8 secondes
        
        // Démarrer l'élection après un court délai
        Thread electionThread = new Thread(() -> {
            try {
                Thread.sleep(1000); // Attendre un peu pour que tous les agents démarrent
                startElection();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
        electionThread.start();
    }
    
    /**
     * Démarrer le processus d'élection du leader
     * Selon l'algorithme Bully : envoyer l'ID à tous les agents avec ID plus élevé
     */
    private void startElection() {
        System.out.println("Agent " + agentId + " démarre une élection");
        
        // Envoyer un message ELECTION à tous les agents avec un ID plus élevé
        boolean higherIdExists = false;
        for (AID agent : this.allAgents) {
            // Extraire l'ID de l'agent à partir du nom
            int otherAgentId = extractIdFromName(agent.getName());
            // Mettre à jour le heartbeat pour cet agent
            updateHeartbeat(otherAgentId);
            if (otherAgentId > agentId) {
                // Envoyer un message ELECTION
                ACLMessage electionMsg = new ACLMessage(ACLMessage.CFP);
                electionMsg.addReceiver(agent);
                electionMsg.setContent("ELECTION:" + agentId);
                send(electionMsg);
                higherIdExists = true;
                System.out.println("Agent " + agentId + " envoie ELECTION à l'agent " + otherAgentId);
            }
        }
        
        // Si aucun agent avec un ID plus élevé n'existe, on devient leader
        if (!higherIdExists) {
            becomeLeader();
        }
    }
    
    /**
     * Devenir le leader du système
     */
    private void becomeLeader() {
        isLeader = true;
        currentLeaderId = agentId;
        System.out.println("Agent " + agentId + " devient LEADER !");
        
        // Informer tous les autres agents qu'on est le leader
        for (AID agent : allAgents) {
            if (!agent.getLocalName().equals(getAID().getLocalName())) { // Ne pas s'envoyer à soi-même
                ACLMessage leaderMsg = new ACLMessage(ACLMessage.INFORM);
                leaderMsg.addReceiver(agent);
                leaderMsg.setContent("LEADER:" + agentId);
                send(leaderMsg);
                System.out.println("Agent " + agentId + " informe les autres qu'il est LEADER");
                
                // Mettre à jour le heartbeat pour cet agent
                updateHeartbeat(extractIdFromName(agent.getName()));
            }
        }
    }
    
    // Cette méthode n'est plus nécessaire car on utilise allAgents maintenant
    /*
    private List<AID> getAllAgents() {
        List<AID> agents = new ArrayList<>();
        
        // Pour cet exemple simple, on suppose que les agents sont nommés de manière prévisible
        // En pratique, on utiliserait un annuaire de services
        for (int i = 0; i < 5; i++) { // Supposons 5 agents possibles
            AID agent = new AID("Agent" + i, false);
            if (!agent.getLocalName().equals(getAID().getLocalName())) {
                agents.add(agent);
            }
        }
        
        return agents;
    }
    */
    
    /**
     * Extraire l'ID de l'agent à partir de son nom
     */
    private int extractIdFromName(String agentName) {
        // Extrait le nombre de la fin du nom de l'agent (ex: Agent1 -> 1)
        try {
            String[] parts = agentName.split("Agent");
            if (parts.length > 1) {
                return Integer.parseInt(parts[1]);
            }
        } catch (NumberFormatException e) {
            // Si le format n'est pas standard, on retourne l'ID actuel
        }
        return agentId;
    }
    
    /**
     * Comportement pour gérer les messages entrants
     */
    private class MessageHandler extends CyclicBehaviour {
        @Override
        public void action() {
            ACLMessage msg = receive();
            if (msg != null) {
                String content = msg.getContent();
                
                if (content.startsWith("ELECTION:")) {
                    handleElectionMessage(content, msg.getSender());
                } else if (content.startsWith("LEADER:")) {
                    handleLeaderMessage(content);
                } else if (content.startsWith("PING:")) {
                    handlePingMessage(content, msg.getSender());
                } else if (content.startsWith("PONG:")) {
                    handlePongMessage(content);
                }
            } else {
                block();
            }
        }
    }
    
    /**
     * Gérer le message PING (heartbeat)
     */
    private void handlePingMessage(String content, AID sender) {
        System.out.println("Agent " + agentId + " reçoit PING de " + sender.getLocalName());
        
        // Extraire l'ID de l'agent qui a envoyé le ping
        try {
            String[] parts = content.split(":");
            if (parts.length > 1) {
                int senderId = Integer.parseInt(parts[1]);
                // Mettre à jour le heartbeat de cet agent
                updateHeartbeat(senderId);
                
                // Répondre avec un PONG
                ACLMessage pong = new ACLMessage(ACLMessage.INFORM);
                pong.addReceiver(sender);
                pong.setContent("PONG:" + agentId);
                send(pong);
                System.out.println("Agent " + agentId + " répond PONG à " + sender.getLocalName());
            }
        } catch (Exception e) {
            System.out.println("Erreur dans le traitement du message PING: " + e.getMessage());
        }
    }
    
    /**
     * Gérer le message PONG (réponse au heartbeat)
     */
    private void handlePongMessage(String content) {
        try {
            String[] parts = content.split(":");
            if (parts.length > 1) {
                int senderId = Integer.parseInt(parts[1]);
                // Mettre à jour le heartbeat de cet agent
                updateHeartbeat(senderId);
                System.out.println("Agent " + agentId + " reçoit PONG de Agent " + senderId);
            }
        } catch (Exception e) {
            System.out.println("Erreur dans le traitement du message PONG: " + e.getMessage());
        }
    }
    
    /**
     * Gérer le message d'élection reçu d'un autre agent
     */
    private void handleElectionMessage(String content, AID sender) {
        try {
            String[] parts = content.split(":");
            int senderId = Integer.parseInt(parts[1]);
            
            System.out.println("Agent " + agentId + " reçoit ELECTION de l'agent " + senderId);
            
            // Comparer les IDs
            if (agentId > senderId) {
                // Répondre avec un message OK si on a un ID plus élevé
                ACLMessage response = new ACLMessage(ACLMessage.PROPOSE);
                response.addReceiver(sender);
                response.setContent("OK:" + agentId);
                send(response);
                System.out.println("Agent " + agentId + " répond OK à l'agent " + senderId);
                
                // On démarre notre propre élection
                startElection();
            }
            // Si notre ID est plus petit, on ne fait rien de plus (on attend le message LEADER)
        } catch (Exception e) {
            System.out.println("Erreur dans le traitement du message d'élection: " + e.getMessage());
        }
    }
    
    /**
     * Gérer le message de leader reçu d'un autre agent
     */
    private void handleLeaderMessage(String content) {
        try {
            String[] parts = content.split(":");
            int leaderId = Integer.parseInt(parts[1]);
            
            System.out.println("Agent " + agentId + " reçoit LEADER: " + leaderId);
            
            // Mettre à jour notre connaissance du leader
            currentLeaderId = leaderId;
            isLeader = (agentId == leaderId);
            
            if (isLeader) {
                System.out.println("Agent " + agentId + " confirme qu'il est LEADER");
            } else {
                System.out.println("Agent " + agentId + " reconnaît l'agent " + leaderId + " comme LEADER");
            }
            
            // Mettre à jour le heartbeat du leader
            updateHeartbeat(leaderId);
        } catch (Exception e) {
            System.out.println("Erreur dans le traitement du message de leader: " + e.getMessage());
        }
    }
    
    /**
     * Découvrir tous les agents dans la plateforme
     * Pour une implémentation réelle, on utiliserait un service d'annuaire
     */
    private void discoverAgents() {
        // Pour cet exemple simple, on suppose que les agents sont nommés de manière prévisible
        for (int i = 0; i < 5; i++) { // Supposons 5 agents possibles
            AID agent = new AID("Agent" + i, false);
            if (!agent.getLocalName().equals(getAID().getLocalName())) {
                allAgents.add(agent);
                // Initialiser le heartbeat pour cet agent
                lastHeartbeat.put(extractIdFromName(agent.getName()), System.currentTimeMillis());
            }
        }
        
        // Créer un agent de backup
        try {
            Object[] backupArgs = new Object[]{agentId + 100}; // ID de backup plus élevé
            jade.wrapper.AgentController backupController = getContainerController().createNewAgent(
                "BackupAgent" + agentId,
                BackupAgent.class.getName(),
                backupArgs
            );
            backupController.start();
            backupAgentAID = new AID("BackupAgent" + agentId, false);
            System.out.println("Backup agent créé pour Agent " + agentId);
        } catch (Exception e) {
            System.err.println("Erreur lors de la création de l'agent de backup: " + e.getMessage());
        }
    }
    
    /**
     * Envoyer un heartbeat (ping) aux autres agents
     */
    public void sendHeartbeat() {
        if (allAgents.isEmpty()) {
            discoverAgents();
        }
        
        for (AID agent : allAgents) {
            ACLMessage ping = new ACLMessage(ACLMessage.REQUEST);
            ping.addReceiver(agent);
            ping.setContent("PING:" + agentId);
            send(ping);
            System.out.println("Agent " + agentId + " envoie PING à " + agent.getLocalName());
        }
    }
    
    /**
     * Mettre à jour le heartbeat d'un agent
     */
    public void updateHeartbeat(int agentId) {
        lastHeartbeat.put(agentId, System.currentTimeMillis());
    }
    
    /**
     * Détecter les agents en panne
     */
    public void detectFailures() {
        long currentTime = System.currentTimeMillis();
        long timeout = 10000; // 10 secondes de timeout
        
        for (Map.Entry<Integer, Long> entry : lastHeartbeat.entrySet()) {
            int agentId = entry.getKey();
            long lastBeat = entry.getValue();
            
            if ((currentTime - lastBeat) > timeout) {
                System.out.println("Panne détectée: Agent " + agentId + " est considéré comme DOWN");
                handleFailure(agentId);
            }
        }
    }
    
    /**
     * Gérer la panne d'un agent
     */
    private void handleFailure(int failedAgentId) {
        // Si le leader est en panne, relancer l'élection
        if (failedAgentId == currentLeaderId) {
            System.out.println("Le leader (Agent " + failedAgentId + ") est en panne. Relance de l'élection...");
            currentLeaderId = -1;
            isLeader = false;
            startElection();
        }
        
        // Activer l'agent de backup
        activateBackup();
    }
    
    /**
     * Activer l'agent de backup
     */
    private void activateBackup() {
        if (backupAgentAID != null) {
            ACLMessage activateMsg = new ACLMessage(ACLMessage.INFORM);
            activateMsg.addReceiver(backupAgentAID);
            activateMsg.setContent("ACTIVATE_BACKUP");
            send(activateMsg);
            System.out.println("Activation de l'agent de backup envoyée");
        }
    }
    
    /**
     * Comportement pour envoyer des heartbeats périodiquement
     */
    private class HeartbeatBehaviour extends TickerBehaviour {
        private CoordinatorAgent agent;
        
        public HeartbeatBehaviour(CoordinatorAgent a, long period) {
            super(a, period);
            this.agent = a;
        }
        
        @Override
        protected void onTick() {
            agent.sendHeartbeat();
        }
    }
    
    /**
     * Comportement pour détecter les pannes d'agents
     */
    private class FailureDetectionBehaviour extends TickerBehaviour {
        private CoordinatorAgent agent;
        
        public FailureDetectionBehaviour(CoordinatorAgent a, long period) {
            super(a, period);
            this.agent = a;
        }
        
        @Override
        protected void onTick() {
            agent.detectFailures();
        }
    }
    
    @Override
    protected void takeDown() {
        System.out.println("Agent " + agentId + " s'arrête");
    }
}