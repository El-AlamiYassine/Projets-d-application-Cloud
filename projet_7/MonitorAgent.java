import jade.core.Agent;
import jade.core.AID;
import jade.core.behaviours.TickerBehaviour;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * MonitorAgent - Agent de surveillance qui envoie des messages heartbeat
 * et détecte les pannes d'agents pour activer un BackupAgent
 */
public class MonitorAgent extends Agent {
  private List<AID> monitoredAgents; // Liste des agents à surveiller
  private List<AID> backupAgents; // Liste des agents de secours
  private Map<String, Long> lastHeartbeat; // Dernière réponse heartbeat reçue
  private Map<String, Boolean> agentStatus; // État des agents (actif/inactif)
  private int heartbeatInterval = 5000; // Intervalle de heartbeat en ms
  private int failureThreshold = 3; // Nombre de PING manqués avant panne
  private Map<String, Integer> missedHeartbeats; // Compteur de PING manqués

  @Override
  protected void setup() {
    System.out.println("MonitorAgent démarré - Surveillance des agents en cours");

    monitoredAgents = new ArrayList<>();
    backupAgents = new ArrayList<>();
    lastHeartbeat = new HashMap<>();
    agentStatus = new HashMap<>();
    missedHeartbeats = new HashMap<>();

    // Initialiser la liste des agents surveillés
    createMonitoredAgentsList();

    // Ajouter un comportement de surveillance périodique (ticker)
    addBehaviour(new TickerBehaviour(this, heartbeatInterval) {
      @Override
      protected void onTick() {
        sendHeartbeat();
        checkForFailures();
      }
    });

    // Ajouter un comportement pour gérer les réponses
    addBehaviour(new ResponseHandler());
  }

  /**
   * Envoyer un message heartbeat (PING) aux agents surveillés
   */
  private void sendHeartbeat() {
    System.out.println("\n=== MonitorAgent: Envoi de PING aux agents surveillés ===");

    // Envoyer un message PING à chaque agent surveillé
    for (AID agent : monitoredAgents) {
      ACLMessage ping = new ACLMessage(ACLMessage.REQUEST);
      ping.addReceiver(agent);
      ping.setContent("PING");
      ping.setConversationId("HEARTBEAT");
      ping.setReplyWith("ping-" + System.currentTimeMillis());
      send(ping);
      System.out.println("MonitorAgent: PING envoyé à " + agent.getLocalName());
    }
  }

  /**
   * Créer la liste des agents à surveiller
   */
  private void createMonitoredAgentsList() {
    // Créer des AID pour les agents à surveiller
    for (int i = 0; i < 3; i++) {
      AID agent = new AID("MainAgent" + i, AID.ISLOCALNAME);
      monitoredAgents.add(agent);
      agentStatus.put(agent.getLocalName(), true);
      missedHeartbeats.put(agent.getLocalName(), 0);
      lastHeartbeat.put(agent.getLocalName(), System.currentTimeMillis());
    }

    // Créer des AID pour les agents de secours
    for (int i = 0; i < 3; i++) {
      AID agent = new AID("BackupAgent" + i, AID.ISLOCALNAME);
      backupAgents.add(agent);
    }

    System.out.println("MonitorAgent: " + monitoredAgents.size() + " agents à surveiller");
    System.out.println("MonitorAgent: " + backupAgents.size() + " agents de secours disponibles");
  }

  /**
   * Comportement pour gérer les réponses et détecter les pannes
   */
  private class ResponseHandler extends CyclicBehaviour {
    @Override
    public void action() {
      // Recevoir les messages de réponse (PONG)
      MessageTemplate template = MessageTemplate.MatchConversationId("HEARTBEAT_RESPONSE");
      ACLMessage msg = receive(template);

      if (msg != null) {
        String content = msg.getContent();
        AID sender = msg.getSender();
        String agentName = sender.getLocalName();

        if ("PONG".equals(content)) {
          System.out.println("MonitorAgent: PONG reçu de " + agentName + " - Agent actif");

          // Mettre à jour le statut de l'agent
          lastHeartbeat.put(agentName, System.currentTimeMillis());
          agentStatus.put(agentName, true);
          missedHeartbeats.put(agentName, 0); // Réinitialiser le compteur
        }
      } else {
        block(500); // Attendre un peu avant de vérifier à nouveau
      }
    }
  }

  /**
   * Vérifier si des agents n'ont pas répondu au PING (panne détectée)
   */
  private void checkForFailures() {
    long currentTime = System.currentTimeMillis();

    for (AID agent : monitoredAgents) {
      String agentName = agent.getLocalName();
      long lastResponse = lastHeartbeat.get(agentName);

      // Si l'agent n'a pas répondu depuis le dernier heartbeat
      if (currentTime - lastResponse > heartbeatInterval) {
        int missed = missedHeartbeats.get(agentName);
        missed++;
        missedHeartbeats.put(agentName, missed);

        System.out.println("MonitorAgent: " + agentName + " n'a pas répondu (" + missed + "/" + failureThreshold + ")");

        // Si le seuil de panne est atteint
        if (missed >= failureThreshold && agentStatus.get(agentName)) {
          System.out.println("\n*** PANNE DÉTECTÉE: " + agentName + " ***");
          agentStatus.put(agentName, false);
          activateBackupAgent(agentName);
        }
      }
    }
  }

  /**
   * Activer un agent de secours pour remplacer l'agent en panne
   */
  private void activateBackupAgent(String failedAgentName) {
    // Trouver un agent de secours disponible
    if (!backupAgents.isEmpty()) {
      AID backupAgent = backupAgents.get(0); // Prendre le premier disponible

      System.out.println("MonitorAgent: Activation de " + backupAgent.getLocalName() +
          " pour remplacer " + failedAgentName);

      // Envoyer un message d'activation à l'agent de secours
      ACLMessage activationMsg = new ACLMessage(ACLMessage.REQUEST);
      activationMsg.addReceiver(backupAgent);
      activationMsg.setContent("ACTIVATE:" + failedAgentName);
      activationMsg.setConversationId("ACTIVATE_BACKUP");
      send(activationMsg);

      // Retirer l'agent de secours de la liste (il est maintenant actif)
      backupAgents.remove(0);
    } else {
      System.out.println("MonitorAgent: ERREUR - Aucun agent de secours disponible!");
    }
  }

  @Override
  protected void takeDown() {
    System.out.println("MonitorAgent: Arrêt de la surveillance");
  }
}