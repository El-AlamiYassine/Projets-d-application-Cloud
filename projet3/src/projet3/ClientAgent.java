package projet3;

import jade.core.Agent;
import jade.core.AID;
import jade.lang.acl.ACLMessage;

public class ClientAgent extends Agent {

  protected void setup() {
    System.out.println("ClientAgent lancé");

    ACLMessage msg = new ACLMessage(ACLMessage.REQUEST);
    msg.addReceiver(new AID("resource", AID.ISLOCALNAME));
    msg.setContent("RESERVE");

    send(msg);
    System.out.println("Message envoyé : RESERVE");
  }
}
