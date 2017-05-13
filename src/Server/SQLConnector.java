package Server;

import org.lwjgl.util.vector.Vector3f;
import engine.toolbox.Log;
import engine.toolbox.Settings;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.ResultSet;

/***
 * Created by pv42 on 11.07.16.
 */
public class SQLConnector {
    private Connection conn;
    public SQLConnector() {
        try {
            Class.forName("com.mysql.jdbc.Driver").newInstance();
            conn = DriverManager.getConnection("jdbc:mysql://" + Settings.SQL_SERVER + "/game?user=" + Settings.SQL_USERNAME + "&password=" +  Settings.SQL_PASSWORD);
        } catch (SQLException ex) {
            Log.e("SQLError\nSQLException: " + ex.getMessage() +"\nSQLState: " + ex.getSQLState() + "\nVendorError: " + ex.getErrorCode());
            ex.printStackTrace();
            System.exit(-1);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public int checkLogin(String username, String password) {
        try {
            Statement s = conn.createStatement();
            ResultSet resultSet = s.executeQuery("SELECT PASSWORD,UID FROM game.users WHERE USERNAME='" + username + "';");
            if (!resultSet.first()) return -1;
            String readPw = resultSet.getString(1);
            Log.i(readPw.equals(password) ? "password correct" : "password wrong");
            if(readPw.equals(password)) {
                return resultSet.getInt(2);
            }else {
                return -1;
            }

        } catch (SQLException e) {
            Log.e("SQLException: " + e.getMessage());
            Log.e("SQLState: " + e.getSQLState());
            Log.e("VendorError: " + e.getErrorCode());
            e.printStackTrace();
        }
        return -1;
    }
    public Vector3f getPosition(int uid) {
        try {
            Statement s = conn.createStatement();
            ResultSet rs = s.executeQuery("SELECT X,Y,Z FROM game.user WHERE UID= " + uid + ";");
            if(rs.next()) {
                return new Vector3f(rs.getFloat(0),rs.getFloat(1),rs.getFloat(2));
            } else {
                return new Vector3f();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return new Vector3f(0,0,0);
    }
    public Vector3f getRotation(int uid) {
        try {
            Statement s = conn.createStatement();
            ResultSet rs = s.executeQuery("SELECT RX,RY,RZ FROM game.user WHERE UID= " + uid + ";");
            if(rs.next()) {
                return new Vector3f(rs.getFloat(0),rs.getFloat(1),rs.getFloat(2));
            } else {
                return new Vector3f();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return new Vector3f(0,0,0);
    }
    public void setPosition(Vector3f position,int uid) {
        try {
            Statement s = conn.createStatement();
            s.execute("UPDATE users SET X=" +  position.x + ", Y=" + position.y + ", Z=" + position.z + " WHERE UID=" + uid + ";");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
