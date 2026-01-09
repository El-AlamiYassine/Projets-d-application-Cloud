import jade.core.Agent;
import jade.core.AID;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

/**
 * MainAgent - Agent principal qui r?pond aux messages heartbeat (PING)
 * et simule une panne pour tester la tol?rance aux pannes
 */
public class MainAgent extends Agent {
  private boolean isAlive = true; // Indique si l'agent est actif
  private int agentId;

  @Override
  protected void setup() {
    Object[] args = getArguments();
    agentId = 0;
    if (args != null && args.length > 0) {
      agentId = (Integer) args[0];
    }

    System.out.println(getAID().getLocalName() + " d?marr? (ID: " + agentId + ")");

    // Ajouter un comportement pour g?rer les messages PING
    addBehaviour(new HeartbeatHandler());

    // Simuler une panne apr?s un certain temps pour tester la tol?rance
    // Seulement pour le premier agent (MainAgent0)
    if (agentId == 0) {
      simulateFailureAfterDelay();
    }
  }

  /**
   * Comportement pour g?rer les messages heartbeat (PING)
   */
  private class HeartbeatHandler extends CyclicBehaviour {
    @Override
    public void action() {
      // Utiliser un template pour matcher les messages PING
      MessageTemplate template = MessageTemplate.and(
          MessageTemplate.MatchPerformative(ACLMessage.REQUEST),
          MessageTemplate.MatchConversationId("HEARTBEAT"));

      ACLMessage msg = receive(template);

      if (msg != null) {
        String content = msg.getContent();
        AID sender = msg.getSender();

        if ("PING".equals(content) && isAlive) {
          // R?pondre avec un PONG si l'agent est actif
          ACLMessage reply = msg.createReply();
          reply.setPerformative(ACLMessage.INFORM);
          reply.setContent("PONG");
          reply.setConversationId("HEARTBEAT_RESPONSE");
          send(reply);
          System.out.println(getAID().getLocalName() + ": PONG envoy? ? " + sender.getLocalName());
        } else if ("PING".equals(content) && !isAlive) {
          // Ne pas r?pondre si l'agent est en panne (simuler une panne)
          System.out.println(getAID().getLocalName() + ": AUCUNE R?PONSE - Agent en panne");
        }
      } else {
        block(1000); // Attendre 1 seconde avant de v?rifier ? nouveau
      }
    }
  }

  /**
   * Simuler une panne d'agent apr?s un certain d?lai
   */
  private void simulateFailureAfterDelay() {
    Thread failureThread = new Thread(() -> {
      try {
        // Attendre 15 secondes avant de simuler la panne
        System.out.println(getAID().getLocalName() + ": Panne simul?e dans 15 secondes...");
        Thread.sleep(15000);

        // Simuler l'arr?t de l'agent
        isAlive = false;
        System.out.println("\n*** " + getAID().getLocalName() + ": SIMULATION DE PANNE - Agent arr?t? ***\n");

        // Attendre encore un peu pour voir la d?tection de panne
        Thread.sleep(20000);

        // Terminer l'agent
        doDelete();
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    });

    failureThread.start();
  }

  @Override
  protected void takeDown() {
    System.out.println(getAID().getLocalName() + ": Agent arr?t?");
  }
}
