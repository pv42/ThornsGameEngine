package engineTester.client;

import java.io.BufferedReader;
import java.io.InputStreamReader;

/**
 * Created by pv42 on 11.07.16.
 */
class TCPClient {
    public static void main(String argv[]) throws Exception {
        String username, password;
        BufferedReader inFromUser = new BufferedReader( new InputStreamReader(System.in));
        username = inFromUser.readLine();
        password = inFromUser.readLine();
        GameClient client = new GameClient();
        if(client.login(username, password)) {
            client.sendPosition(0,1,0);
            client.sendRotation(0,90,30);
        }
        client.close();
    }
}