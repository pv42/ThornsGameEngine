package engine.toolbox;


import org.ini4j.Ini;

import java.io.File;
import java.io.IOException;

/***
 * Created by pv42 on 21.06.16.
 */
public class Settings {
    private Settings() { // can not be created
    }
    //self
    private static final String CONFIG_FILE = "conf.ini";
    private static final String TAG = "Settings";
    //graphics
    public static int MULTI_SAMPLE_ANTI_ALIASING = 4; //todo
    public static int FPS_LIMIT = 60;
    public static int WIDTH = 600; //not fullscreen
    public static int HEIGHT = 600;
    public static int WIDTH_FULLSCREEN = 1920; //todo not hard codr
    public static int HEIGHT_FULLSCREEN = 1080;
    public static int ANISOTROPIC_FILTERING = 1;
    public static int MAX_PARTICLE_INSTANCES = 10000;
    public static float AMBIENT_LIGHT = .1f;
    public static Color SKY_COLOR = new Color(0.1, 0.12, 0.128);
    //environment
    public static final float GRAVITY = 10; //ms^-2
    //input
    public static final float MOUSE_SENSITIVITY = 0.4f;
    //camera
    public static final float FOV = 70;
    public static final float NEAR_PLANE = 0.1f;
    public static final float FAR_PLANE = 2000;
    //animation
    public static int MAX_BONES_PER_VERTEX = 4;
    public static int MAX_BONES = 250;
    public static boolean SHOW_SKELETON_BONES = false;
    //debug
    public static boolean SHOW_DEBUG_LOG = false;
    public static final boolean WRITE_LOG_FILE = false;
    public static final String LOG_PATH = "log/log.txt";
    //network
    public static final int NETWORK_TIMEOUT = 20; //ms between nw syncs
    public static final String SQL_USERNAME = "root";
    public static final String SQL_PASSWORD = "unde47.M";
    public static final String SERVER_ADDRESS = "localhost";
    public static final String SERVER_USERNAME = "root";
    public static final String SERVER_PASSWORD = "toor";
    public static final String SQL_SERVER = "192.168.178.21";
    public static final long LEVEL_FILE_VERSION = 1000000; //x.yy.zzzz -> xyyzzzz

    static {
        loadSettings();
    }

    public static void loadSettings() {
        Ini ini = new Ini();
        File f = new File(CONFIG_FILE);
        if(f.exists()) {
            try {
                ini.load(f);
            } catch (IOException e) {
                e.printStackTrace();
            }
            SHOW_DEBUG_LOG = Boolean.parseBoolean(ini.get("log", "show_debug_log"));
            Log.d(TAG,"loaded settings");
        } else {
            ini.put("log","show_debug_log",SHOW_DEBUG_LOG);
            try {
                ini.store(f);
            } catch (IOException e) {
                e.printStackTrace();
            }
            Log.i(TAG,"settings crated");
        }

    }
}
