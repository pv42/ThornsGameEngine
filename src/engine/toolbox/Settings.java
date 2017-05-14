package engine.toolbox;

/***
 * Created by pv42 on 21.06.16.
 */
public class Settings {
    private Settings() {  } // can not be created
    //graphics
    public static final int MULTI_SAMPLE_ANTI_ALIASING = 4; //todo
    public static final int FPS_LIMIT = 60;
    public static final int WIDTH = 600; //not fullscreen
    public static final int HEIGHT = 600;
    public static final int WIDTH_FULLSCREEN = 1920;
    public static final int HEIGHT_FULLSCREEN = 1080;
    public static final int ANISOTROPIC_FILTERING = 1;
    public static final int MAX_PARTICLE_INSTANCES = 10000;
    public static final float AMBIENT_LIGHT = .1f;
    //environment
    public static final float GRAVITY = -10; //ms^-2
    //input
    public static final float MOUSE_SENSITIVITY = 0.4f;
    //camera
    public static final float FOV = 70;
    public static final float NEAR_PLANE = 0.1f;
    public static final float FAR_PLANE = 2000;
    //animation
    public static final int MAX_BONES_PER_VERTEX = 4;
    public static final int MAX_BONES = 250;
    public static final boolean SHOW_SKELETON_BONES = false;
    //debug
    public static final boolean SHOW_DEBUG_LOG = false;
    public static final boolean WRITE_LOG_FILE = false;
    //network
    public static final int NETWORK_TIMEOUT = 20; //ms between nw syncs
    public static final String SQL_USERNAME = "root";
    public static final String SQL_PASSWORD = "unde47.M";
    public static final String SERVER_ADDRESS = "localhost";
    public static final String SERVER_USERNAME = "root";
    public static final String SERVER_PASSWORD = "toor";
    public static final String SQL_SERVER = "192.168.178.21";
    public static final long LEVEL_FILE_VERSION = 1000000; //x.yy.zzzz -> xyyzzzz
}
