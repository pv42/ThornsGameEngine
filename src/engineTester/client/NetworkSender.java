package engineTester.client;

import engine.graphics.entities.Player;
import engine.toolbox.Settings;

/**
 * Created by pv42 on 11.07.16.
 */
public class NetworkSender extends Thread{
    private Player player;
    private GameClient client;
    private boolean end;
    public NetworkSender(Player player,GameClient client) {
        this.player = player;
        this.client = client;
        end = false;
    }
    @Override
    public void run() {
        while (!end) {
            client.sendPosition(player.getPosition().x, player.getPosition().y, player.getPosition().z);
            client.sendRotation(player.getRx(), player.getRy(), player.getRz());
            try {
                Thread.sleep(Settings.NETWORK_TIMEOUT);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
    public void end() {
        end = true;
    }
}
