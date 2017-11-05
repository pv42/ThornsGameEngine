package engine;

import engine.audio.AudioMaster;
import engine.inputs.*;
import engine.graphics.particles.ParticleMaster;
import engine.graphics.display.DisplayManager;
import engine.graphics.renderEngine.Loader;
import engine.graphics.renderEngine.MasterRenderer;
import engine.toolbox.Log;
import engine.toolbox.Settings;
import org.lwjgl.Version;
import org.lwjgl.glfw.GLFW;

/***
 * Created by pv42 on 07.09.2016.
 */
public class EngineMaster {
    private static final String TAG = "Engine";
    public static void init() {
        init(false);
    }
    public static void init(boolean use2D) {
        Settings.loadSettings();
        if(Settings.WRITE_LOG_FILE) Log.connectLogFile();
        Log.i(TAG, "OS: " + org.lwjgl.system.Platform.get().getName());
        Log.i(TAG, "lwjgl-version: " + Version.getVersion());
        AudioMaster.init();
        DisplayManager.init();
        long windowID = DisplayManager.createWindow().getId();
        MasterRenderer.init(use2D);
        ParticleMaster.init(MasterRenderer.getProjectionMatrix());
        InputLoop.init(windowID);
        new Thread(InputLoop::run).start(); //starts input handling threat
    }
    public static void finish() {
        InputLoop.finish();
        Log.i(TAG,"shutting down render ");
        ParticleMaster.cleanUp();
        MasterRenderer.cleanUp();
        Loader.cleanUp();
        DisplayManager.destroyDisplay();
        AudioMaster.cleanUp();
        GLFW.glfwTerminate();
        Log.i(TAG,"program ended");
    }
}
