package projet3;

public class Main {
  public static void main(String[] args) {
    try {
      String[] jadeArgs = {
          "-gui",
          "-port", "12001",
          "-agents", "resource:projet3.ResourceAgent;client1:projet3.Client1Agent;client2:projet3.Client2Agent"
      };

      jade.Boot.main(jadeArgs);

    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
