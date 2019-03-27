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
    public static int LIMIT_FPS = 0; // no limit
    public static int DEFAULT_WIDTH = 600; //not fullscreen
    public static int DEFAULT_HEIGHT = 600;
    public static int ANISOTROPIC_FILTERING = 1;
    public static int MAX_PARTICLE_INSTANCES = 10000;
    public static float AMBIENT_LIGHT = .1f;
    public static Color SKY_COLOR = new Color(0.1, 0.12, 0.128);
    public static Integer MSAA = 2;
    //environment
    public static final float GRAVITY = 10; //ms^-2
    //input
    public static final float MOUSE_SENSITIVITY = 0.4f;
    //camera
    public static final float FOV = 70;
    public static final float NEAR_PLANE = 0.1f;
    public static final float FAR_PLANE = 2000;
    //animation
    public static int MAX_BONES_PER_VERTEX = 3;
    public static int MAX_BONES = 250;
    public static boolean SHOW_SKELETON_BONES = false;
    //debug
    public static boolean SHOW_DEBUG_LOG = true;
    public static final boolean WRITE_LOG_FILE = false;
    public static boolean SHOW_EVENT_LOG = false;
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
    private static final String KEY_VSYNC = "vsync";
    private static final String KEY_MSAA = "msaa";
    private static final String KEY_WIDTH = "width";
    private static final String KEY_HEIGHT = "height";
    private static final String KEY_ANIF = "anisotropic_filtering";
    private static final String KEY_MAX_PARTICLES = "maximum_particles";
    private static final String KEY_AMBIENT = "ambient_light";
    private static final String SECTION_LOG = "log";
    private static final String KEY_SDL = "show_debug_log";
    private static final String KEY_SEL = "show_event_log";

    public static void loadSettings() {
        ini = new Ini();
        File f = new File(CONFIG_FILE);
        if(f.exists()) {
            loadIni(f);
        } else {
            Log.i(TAG,"settings don't exist creating");
        }
        MSAA = Integer.parseInt(getSetting(SECTION_GRAPHIC, KEY_MSAA, MSAA));
        LIMIT_FPS      = Integer.parseInt(getSetting(SECTION_GRAPHIC, KEY_VSYNC, LIMIT_FPS));
        DEFAULT_WIDTH = Integer.parseInt(getSetting(SECTION_GRAPHIC, KEY_WIDTH, DEFAULT_WIDTH));
        DEFAULT_HEIGHT = Integer.parseInt(getSetting(SECTION_GRAPHIC, KEY_HEIGHT, DEFAULT_HEIGHT));
        ANISOTROPIC_FILTERING = Integer.parseInt(getSetting(SECTION_GRAPHIC, KEY_ANIF, ANISOTROPIC_FILTERING));
        MAX_PARTICLE_INSTANCES = Integer.parseInt(getSetting(SECTION_GRAPHIC, KEY_MAX_PARTICLES, MAX_PARTICLE_INSTANCES));
        AMBIENT_LIGHT  = Float.parseFloat(getSetting(SECTION_GRAPHIC, KEY_AMBIENT, AMBIENT_LIGHT));
        SHOW_DEBUG_LOG = Boolean.parseBoolean(getSetting(SECTION_LOG, KEY_SDL, SHOW_DEBUG_LOG));
        SHOW_EVENT_LOG = Boolean.parseBoolean(getSetting(SECTION_LOG, KEY_SEL, SHOW_EVENT_LOG));
        Log.d(TAG,"loaded settings");
        storeIni(f);

    }
    private static String getSetting(String section, String key, Object defaultValue) {
        if(ini.get(section) != null) {
            if(ini.get(section).containsKey(key)) {
                return ini.get(section,key);
            }
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
