package projet3;

import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;

public class ResourceAgent extends Agent {

  private boolean available = true;

  protected void setup() {
    System.out.println("ResourceAgent lancé");

    addBehaviour(new CyclicBehaviour() {
      public void action() {

        ACLMessage msg = receive();

        if (msg != null) {
          System.out.println("Message reçu : " + msg.getContent());

          ACLMessage reply = msg.createReply();

          if (available) {
            available = false;
            reply.setContent("ACCETER");
            System.out.println("Resource reserved for client: " + msg.getSender().getName());
          } else {
            reply.setContent("REFUSER");
            System.out.println("Resource is busy, request from client: " + msg.getSender().getName());
          }

          send(reply);
        } else {
          block();
        }
      }
    });
  }
}
