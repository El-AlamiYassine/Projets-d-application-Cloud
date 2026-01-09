import jade.core.Agent;
import jade.core.AID;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

/**
 * BackupAgent - Agent de secours activé quand un agent principal tombe en panne
 */
public class BackupAgent extends Agent {
  private boolean isActive = false; // Indique si l'agent est actif (en remplacement)
  private String replacedAgent = ""; // Nom de l'agent remplacé
  private int agentId;

  @Override
  protected void setup() {
    Object[] args = getArguments();
    agentId = 0;
    if (args != null && args.length > 0) {
      agentId = (Integer) args[0];
    }

    System.out.println(getAID().getLocalName() + " démarré (ID: " + agentId + ") - En attente d'activation");

    // Ajouter un comportement pour gérer les messages d'activation
    addBehaviour(new ActivationHandler());

    // Ajouter un comportement pour gérer les messages PING quand actif
    addBehaviour(new HeartbeatHandler());
  }

  /**
   * Comportement pour gérer les messages d'activation
   */
  private class ActivationHandler extends CyclicBehaviour {
    @Override
    public void action() {
      // Utiliser un template pour matcher les messages d'activation
      MessageTemplate template = MessageTemplate.MatchConversationId("ACTIVATE_BACKUP");

      ACLMessage msg = receive(template);

      if (msg != null) {
        String content = msg.getContent();
        AID sender = msg.getSender();

        if (content.startsWith("ACTIVATE:")) {
          // Extraire le nom de l'agent à remplacer
          String[] parts = content.split(":");
          if (parts.length > 1) {
            replacedAgent = parts[1];

            // Activer l'agent de secours
            isActive = true;

            System.out.println("\n*** " + getAID().getLocalName() +
                ": ACTIVATION - Remplace " + replacedAgent + " ***\n");

            // Répondre pour confirmer l'activation
            ACLMessage reply = msg.createReply();
            reply.setPerformative(ACLMessage.INFORM);
            reply.setContent("BACKUP_ACTIVE:" + getAID().getLocalName());
            send(reply);
          }
        }
      } else {
        block(1000); // Attendre 1 seconde avant de vérifier à nouveau
      }
    }
  }

  /**
   * Comportement pour gérer les messages heartbeat (PING) quand l'agent est actif
   */
  private class HeartbeatHandler extends CyclicBehaviour {
    @Override
    public void action() {
      // Utiliser un template pour matcher les messages PING
      MessageTemplate template = MessageTemplate.and(
          MessageTemplate.MatchPerformative(ACLMessage.REQUEST),
          MessageTemplate.MatchConversationId("HEARTBEAT"));

      ACLMessage msg = receive(template);

      if (msg != null && isActive) {
        String content = msg.getContent();
        AID sender = msg.getSender();

        if ("PING".equals(content)) {
          // Répondre avec un PONG si l'agent est actif
          ACLMessage reply = msg.createReply();
          reply.setPerformative(ACLMessage.INFORM);
          reply.setContent("PONG");
          reply.setConversationId("HEARTBEAT_RESPONSE");
          send(reply);
          System.out.println(getAID().getLocalName() +
              " (remplace " + replacedAgent + "): PONG envoyé à " +
              sender.getLocalName());
        }
      } else {
        block(1000); // Attendre 1 seconde avant de vérifier à nouveau
      }
    }
  }

  @Override
  protected void takeDown() {
    System.out.println(getAID().getLocalName() + ": Agent de secours arrêté");
  }

  // Getters pour les informations sur l'état de l'agent
  public boolean isActive() {
    return isActive;
  }

  public String getReplacedAgent() {
    return replacedAgent;
  }
}