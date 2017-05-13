package engine;

import engine.audio.AudioMaster;
import engine.inputs.*;
import engine.graphics.particles.ParticleMaster;
import engine.graphics.renderEngine.DisplayManager;
import engine.graphics.renderEngine.Loader;
import engine.graphics.renderEngine.MasterRenderer;
import engine.toolbox.Log;
import engine.toolbox.Settings;
import org.lwjgl.glfw.GLFW;

/***
 * Created by pv42 on 07.09.2016.
 */
public class EngineMaster {
    private static final String TAG = "Engine";
    //private static GLFWErrorCallback errorCallback;
    public static void init() {
        if(Settings.WRITE_LOG_FILE) Log.connectLogFile();
        Log.i(TAG, "OS: " + org.lwjgl.system.Platform.get().getName());
        Log.i(TAG, "lwjgl-version: " + org.lwjgl.Version.getVersion());
        //todo glfw errors
        if(!GLFW.glfwInit()) {
            throw new IllegalStateException("GLFW init failed");
        }
        AudioMaster.init();
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
