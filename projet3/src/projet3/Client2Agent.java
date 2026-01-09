package projet3;

import jade.core.Agent;
import jade.core.AID;
import jade.lang.acl.ACLMessage;

public class Client2Agent extends Agent {

  protected void setup() {
    System.out.println("Client2Agent lancé");

    ACLMessage msg = new ACLMessage(ACLMessage.REQUEST);
    msg.addReceiver(new AID("resource", AID.ISLOCALNAME));
    msg.setContent("RESERVE_CLIENT2");

    send(msg);
    System.out.println("Message envoyé par Client2 : RESERVE_CLIENT2");
  }
}