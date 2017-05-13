package Server;

import engine.toolbox.Settings;

import java.net.*;
import java.util.ArrayList;
import java.util.List;

class GameServer {
	private static final int PORT = 6789;
	public static void main(String args[]) throws Exception {
		new GameServer();
	}
	public GameServer() throws Exception{
        //debug
		SQLConnector sqlc = new SQLConnector();
		sqlc.checkLogin(Settings.SERVER_USERNAME, Settings.SERVER_PASSWORD);
		//end debud
		ServerSocket serverSocket  = new ServerSocket(PORT);
        List<PlayerConnectionHandle> playerConnectionHandles = new ArrayList<>();
        while(true) {
			Socket connectionSocket = serverSocket.accept();
			PlayerConnectionHandle t = new PlayerConnectionHandle(connectionSocket);
            //for (PlayerConnectionHandle pch : playerConnectionHandles) {
            //    if(!pch.isAlive()) playerConnectionHandles.remove(pch); //// TODO: 19.08.2016 works?
			//}
            playerConnectionHandles.add(t);
			new Thread(t).start();
		}
	}
}
