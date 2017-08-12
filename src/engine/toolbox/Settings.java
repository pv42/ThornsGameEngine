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
    public static int FPS_LIMIT = 60;
    public static int WIDTH = 600; //not fullscreen
    public static int HEIGHT = 600;
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

    //new ini stuff
    private static Ini ini;
    private static final String SECTION_GRAPHIC = "graphic";
    private static final String KEY_FPS = "fps_cap";
    private static final String KEY_WIDTH = "width";
    private static final String KEY_HEIGHT = "height";
    private static final String KEY_ANIF = "anisotropic_filtering";
    private static final String KEY_MAXPAR = "maximum_particles";
    private static final String KEY_AMBINET = "ambient_light";
    private static final String SECTION_LOG = "log";
    private static final String KEY_SDL = "show_debug_log";

    public static void loadSettings() {
        ini = new Ini();
        File f = new File(CONFIG_FILE);
        if(f.exists()) {
            loadIni(f);
        } else {
            Log.i(TAG,"settings don't exist creating");
        }
        FPS_LIMIT      = Integer.parseInt(    getSetting(SECTION_GRAPHIC, KEY_FPS, FPS_LIMIT));
        WIDTH          = Integer.parseInt(    getSetting(SECTION_GRAPHIC, KEY_WIDTH, WIDTH));
        HEIGHT         = Integer.parseInt(    getSetting(SECTION_GRAPHIC, KEY_HEIGHT, HEIGHT));
        ANISOTROPIC_FILTERING = Integer.parseInt(getSetting(SECTION_GRAPHIC, KEY_ANIF, ANISOTROPIC_FILTERING));
        MAX_PARTICLE_INSTANCES = Integer.parseInt(getSetting(SECTION_GRAPHIC, KEY_MAXPAR, MAX_PARTICLE_INSTANCES));
        AMBIENT_LIGHT  = Float.parseFloat(     getSetting(SECTION_GRAPHIC, KEY_AMBINET, AMBIENT_LIGHT));
        SHOW_DEBUG_LOG = Boolean.parseBoolean(getSetting(SECTION_LOG, KEY_SDL, SHOW_DEBUG_LOG));
        Log.d(TAG,"loaded settings");
        storeIni(f);

    }
    private static String getSetting(String section, String key, Object defaultValue) {
        if(ini.containsKey(key)) {
            return ini.get(section,key);
        }
        ini.put(section,key,defaultValue);
        return defaultValue.toString();
    }
    private static void loadIni(File f) {
        try {
            ini.load(f);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private static void storeIni(File f) {
        try {
            ini.store(f);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
