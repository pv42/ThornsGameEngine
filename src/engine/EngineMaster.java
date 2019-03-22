package engine;

import engine.audio.AudioMaster;
import engine.graphics.glglfwImplementation.GLLoader;
import engine.graphics.glglfwImplementation.display.GLFWDisplayManager;
import engine.graphics.glglfwImplementation.display.GLFWWindow;
import engine.graphics.particles.ParticleMaster;
import engine.graphics.glglfwImplementation.MasterRenderer;
import engine.inputs.InputLoop;
import engine.toolbox.Log;
import engine.toolbox.Settings;
import org.lwjgl.Version;
import org.lwjgl.glfw.GLFW;

/***
 * Engines main class to start and stop engines components
 *
 * @author pv42
 *
 * Created by pv42 on 07.09.2016.
 */
public class EngineMaster {
    private static final String TAG = "Engine";
    private static GLFWWindow window;
    private static GLFWDisplayManager displayManager;

    public static GLFWWindow init() {
        return init(false);
    }


    /**
     * starts the engine, initialize components, opens window, start event handling, must be called as first operation of the engine
     *
     * @param use2D use flat projection
     */
    public static GLFWWindow init(boolean use2D) {
        Settings.loadSettings();
        if (Settings.WRITE_LOG_FILE) Log.connectLogFile();
        Log.i(TAG, "OS: " + org.lwjgl.system.Platform.get().toString());
        Log.i(TAG, "lwjgl-version: " + Version.getVersion());
        AudioMaster.init();
        displayManager = new GLFWDisplayManager();
        window = displayManager.createWindow();
        window.makeActive();
        window.show();
        MasterRenderer.init(window, use2D);
        ParticleMaster.init(MasterRenderer.getProjectionMatrix());
        InputLoop.init(window.getId());
        new Thread(InputLoop::run).start(); //starts input handling threat
        return window;
    }

    /**
     * stops the engine, frees resources, exits
     */
    public static void finish() {
        InputLoop.finish();
        Log.i(TAG, "shutting down render ");
        ParticleMaster.cleanUp();
        MasterRenderer.cleanUp();
        GLLoader.cleanUp();
        displayManager.cleanUp();
        AudioMaster.cleanUp();
        GLFW.glfwTerminate();
        Log.i(TAG, "program stopped");
    }
}
