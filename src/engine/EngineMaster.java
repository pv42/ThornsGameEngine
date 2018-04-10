package engine;

import engine.audio.AudioMaster;
import engine.graphics.display.Window;
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
    private static Window window;
    public static Window init() {
        return init(false);
    }

    /**
     * starts the engine, initialize components, opens window, start event handling
     * @param use2D use flat projection
     */
    public static Window init(boolean use2D) {
        Settings.loadSettings();
        if(Settings.WRITE_LOG_FILE) Log.connectLogFile();
        Log.i(TAG, "OS: " + org.lwjgl.system.Platform.get().toString());
        Log.i(TAG, "lwjgl-version: " + Version.getVersion());
        AudioMaster.init();
        DisplayManager.init();
        window = DisplayManager.createWindow();
        MasterRenderer.init(use2D);
        ParticleMaster.init(MasterRenderer.getProjectionMatrix());
        InputLoop.init(window.getId());
        //new Thread(InputLoop::run).start(); //starts input handling threat
        return window;
    }

    /**
     * stops the engine, frees resources, exits
     */
    public static void finish() {
        InputLoop.finish();
        Log.i(TAG,"shutting down render ");
        ParticleMaster.cleanUp();
        MasterRenderer.cleanUp();
        Loader.cleanUp();
        window.destroy();
        DisplayManager.destroy();
        AudioMaster.cleanUp();
        GLFW.glfwTerminate();
        Log.i(TAG,"program stopped");
    }
}
