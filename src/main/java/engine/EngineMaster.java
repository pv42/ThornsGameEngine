package engine;

import engine.audio.AudioMaster;
import engine.graphics.display.DisplayManager;
import engine.graphics.glglfwImplementation.GLLoader;
import engine.graphics.glglfwImplementation.display.GLFWDisplayManager;
import engine.graphics.glglfwImplementation.text.GLTTFontFactory;
import engine.graphics.glglfwImplementation.textures.GLTextureLoader;
import engine.graphics.particles.ParticleMaster;
import engine.graphics.glglfwImplementation.MasterRenderer;
import engine.graphics.text.FontFactory;
import engine.graphics.textures.TextureLoader;
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
    private static GLFWDisplayManager displayManager;
    private static TextureLoader textureLoader;
    private static FontFactory fontFactory;

    public static boolean init() {
        return init(false);
    }


    /**
     * starts the engine, initialize components, opens window, start event handling, must be called as first operation of the engine
     *
     * @param use2D use flat projection
     * @return {@code true} if the initialisation was successful, {@code false} if an error occurred during
     * initialisation
     */
    public static boolean init(boolean use2D) {
        Settings.loadSettings();
        if (Settings.WRITE_LOG_FILE) Log.connectLogFile();
        Log.i(TAG, "OS: " + org.lwjgl.system.Platform.get().toString());
        Log.i(TAG, "lwjgl-version: " + Version.getVersion());
        AudioMaster.init();
        try {
            displayManager = new GLFWDisplayManager();
        } catch (IllegalStateException ex) {
            AudioMaster.cleanUp();
            Log.w(TAG, "engine stopped");
            return false;
        }
        textureLoader = new GLTextureLoader();
        MasterRenderer.init(use2D);
        fontFactory = new GLTTFontFactory();
        return true;
    }

    /**
     * get the engine current display-/windowmanager
     *
     * @return engine display manager
     */
    public static DisplayManager getDisplayManager() {
        return displayManager;
    }

    /**
     * stops the engine, frees resources, exits
     */
    public static void finish() {
        InputLoop.finish();
        Log.i(TAG, "shutting down render ");
        ParticleMaster.cleanUp();
        if(fontFactory != null) fontFactory.clear();
        MasterRenderer.cleanUp();
        GLLoader.cleanUp();
        if(displayManager != null) displayManager.cleanUp();
        if(textureLoader != null) textureLoader.cleanUp();
        AudioMaster.cleanUp();
        GLFW.glfwTerminate();
        Log.i(TAG, "engine stopped");
    }

    public static TextureLoader getTextureLoader() {
        return textureLoader;
    }

    public static FontFactory getFontFactory() {
        return fontFactory;
    }
}
