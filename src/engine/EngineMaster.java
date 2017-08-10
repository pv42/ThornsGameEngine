package engine;

import engine.audio.AudioMaster;
import engine.inputs.*;
import engine.graphics.particles.ParticleMaster;
import engine.graphics.DisplayManager;
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
        if(Settings.WRITE_LOG_FILE) Log.connectLogFile();
        Log.i(TAG, "OS: " + org.lwjgl.system.Platform.get().getName());
        Log.i(TAG, "v:" + Version.getVersion());
        Log.i(TAG, "lwjgl-version: " + Version.VERSION_MAJOR + "." +  Version.VERSION_MINOR + "." +
                Version.VERSION_REVISION );
        AudioMaster.init();
        DisplayManager.init();
        long windowID = DisplayManager.createDisplay();
        MasterRenderer.init();
        ParticleMaster.init(MasterRenderer.getProjectionMatrix());
        InputLoop.init(windowID);
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
