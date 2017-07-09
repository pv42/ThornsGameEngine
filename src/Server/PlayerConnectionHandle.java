package Server;



import org.joml.Vector3f;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.Random;

/**
 * Created by pv42 on 11.07.16.
 */
class PlayerConnectionHandle implements Runnable {
    private Socket socket;
    private Vector3f position;
    private Vector3f rotation;
    private int uid = -1;
    private SQLConnector sqlconn;
    public PlayerConnectionHandle(Socket socket) {
        this.socket = socket;
        sqlconn = new SQLConnector();
    }
    @Override
    public void run(){
        try {
            boolean isLoggedIn = false;
            long sesseinId = -1;
            int uid = -1;
            position = new Vector3f();
            do {
                DataOutputStream outToClient = new DataOutputStream(socket.getOutputStream());
                DataInputStream din = new DataInputStream(socket.getInputStream());
                char c = din.readChar();
                switch(c) {
                    case 'l' :
                        if(!isLoggedIn){
                            BufferedReader bdin = new BufferedReader(new InputStreamReader(din));
                            String usr = bdin.readLine();
                            String pwd = bdin.readLine();
                            uid = sqlconn.checkLogin(usr,pwd);
                            if(uid != -1) {
                                System.out.println("User " + usr + " logged in");
                                sesseinId = randomLong();
                                isLoggedIn = true;
                                outToClient.writeBytes(sesseinId + "\n");
                                position = sqlconn.getPosition(uid);
                                outToClient.writeFloat(position.x);
                                outToClient.writeFloat(position.y);
                                outToClient.writeFloat(position.z);

                            } else {
                                System.out.println("User login failed");
                                outToClient.writeBytes("-1\n");
                            }
                        }
                        break;
                    case 'p' :
                        position.x = din.readFloat();
                        position.y = din.readFloat();
                        position.z = din.readFloat();
                        outToClient.writeBytes("ok\n");
                        System.out.println("Position:" + position.x + "," + position.y + "," + position.z);
                        break;
                    case 'r' :
                        float rx = din.readFloat();
                        float ry = din.readFloat();
                        float rz = din.readFloat();
                        outToClient.writeBytes("ok\n");
                        System.out.println("Rotation:" + rx + "," + ry + "," + rz);
                        break;
                    case 'e' :
                        sqlconn.setPosition(position,uid);
                        outToClient.writeBytes("ok\n");
                        System.out.println("END");
                        isLoggedIn = false;
                        break;
                    default:
                        System.out.println(c);
                }
            } while (isLoggedIn);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Vector3f getPosition() {
        return position;
    }

    public Vector3f getRotation() {
        return rotation;
    }

    private static long randomLong(){
        Random r = new Random();
        long l = r.nextLong() * 0x871;
        l /= 2;
        return l;
    }
}