package engineTester.client;


import org.joml.Vector3f;
import engine.toolbox.Log;
import engine.toolbox.Settings;

import java.io.*;
import java.net.Socket;

public class GameClient {
	public static int PORT = 6789;
	Socket clientSocket;
	DataOutputStream outToServer;
	DataInputStream inFromServer;
	long loginToken = -1; 
	public GameClient() {

	}
    public boolean connect() {
        Log.i("starting TCP Client");
        try {
            clientSocket = new Socket(Settings.SERVER_ADDRESS, PORT);
            outToServer = new DataOutputStream(clientSocket.getOutputStream());
            inFromServer = new DataInputStream(clientSocket.getInputStream());
            Log.i("started TCP Client");
            return true;
        } catch (IOException e) {
            Log.e("couldnt connect to server: " + e.getMessage());
        }
        return false;
    }
	public boolean login(String username, String password) {
		Log.i("login to server");
		try {
			outToServer.writeChar('l');
			outToServer.writeBytes(username + "\n");
			outToServer.writeBytes(password + "\n");
			String answer = inFromServer.readLine();
			if(Long.valueOf(answer) == -1) {
				Log.e("login failed: username and password don't match");
				return false;
			}
			loginToken = Long.valueOf(answer);
		} catch (IOException e){
			e.printStackTrace();
		}
		return true;
		
	}
	public void sendPosition(float x, float y, float z) {
		try{
			outToServer.writeChar('p');
			outToServer.writeFloat(x);
			outToServer.writeFloat(y);
			outToServer.writeFloat(z);
			String msg = inFromServer.readLine();
		} catch (IOException e){
			e.printStackTrace();
		}
	}
	public void sendRotation(float rx, float ry, float rz) {
		try {
			outToServer.writeChar('r');
			outToServer.writeFloat(rx);
			outToServer.writeFloat(ry);
			outToServer.writeFloat(rz);
			String msg  = inFromServer.readLine();
		} catch (IOException e){
			e.printStackTrace();
		}
	}
	public Vector3f readPosition() {
		Vector3f position = new Vector3f();

		try {
			position.x = inFromServer.readFloat();
			position.y = inFromServer.readFloat();
			position.z = inFromServer.readFloat();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return position;
	}
	public void close() {
		try {
			if(loginToken != -1) {
				System.out.println("se");
				outToServer.writeChar('e');
				System.out.println(inFromServer.readLine());
			}
			clientSocket.close();
		} catch (IOException e){
			e.printStackTrace();
		}
	}
}