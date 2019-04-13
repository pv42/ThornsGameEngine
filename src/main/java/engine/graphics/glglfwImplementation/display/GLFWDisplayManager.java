package engine.graphics.glglfwImplementation.display;

import engine.graphics.display.DisplayManager;
import engine.graphics.glglfwImplementation.MasterRenderer;
import engine.graphics.particles.ParticleMaster;
import engine.toolbox.Log;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.GL43;
import org.lwjgl.opengl.GLCapabilities;
import org.lwjgl.opengl.GLDebugMessageCallback;
import org.lwjgl.system.MemoryUtil;

import java.util.ArrayList;
import java.util.List;

import static engine.toolbox.Settings.DEFAULT_HEIGHT;
import static engine.toolbox.Settings.DEFAULT_WIDTH;
import static engine.toolbox.Settings.MSAA;
import static org.lwjgl.glfw.GLFW.GLFW_SAMPLES;
import static org.lwjgl.opengl.GL13.GL_SAMPLES;

/**
 * Manages GLFW contexts and windows
 *
 * @author pv42
 */
public class GLFWDisplayManager implements DisplayManager {
    private static final String TAG = "DisplayManager";
    private static final String DEFAULT_TITLE = "Thorns";
    private static final int NV_BUFFER_USE_VRAM = 0x20071; // nVidia driver's OpenGL debug id for using vram for vaos
    private static final boolean OPENGL_REDUCED_ERROR_OUTPUT = false; // requires more work
    private static List<GLFWWindow> windows = new ArrayList<>();
    private static String lastOpenGLError;
    private static int lastErrorCount;

    /**
     * initialize GLFW, setting up the error callback and some settings
     */
    public GLFWDisplayManager() {
        GLFWErrorCallback.createPrint(System.err).set();
        if (!GLFW.glfwInit()) {
            throw new IllegalStateException("Couldn't initialize GLFW");
        }
        GLFW.glfwWindowHint(GLFW_SAMPLES, MSAA);
        Log.i(TAG, "initialized");

    }

    /**
     * creates the open GL context along with an error callback
     *
     * @throws IllegalStateException Throws exception if the openGL version 3.0 is not supported.
     */
    private static void createGLContext() {
        GLCapabilities capabilities = GL.createCapabilities();
        Log.i(TAG, "openGL version: " + GL11.glGetString(GL11.GL_VERSION));
        if (!capabilities.OpenGL30 || GL11.glGetInteger(GL30.GL_MAJOR_VERSION) < 3) // two version test since some Intel chips lie
            throw new IllegalStateException("openGL version 3.0 or greater required");
        if (capabilities.OpenGL43) {
            GL11.glEnable(GL43.GL_DEBUG_OUTPUT);
            GL43.glDebugMessageCallback((int source, int type, int id, int severity, int length, long msg, long userParam) -> {
                /*
                 * read the message from memory, necessary since the callback invoke only provides the messages memory
                 * address and length, freeing this memory is handled by openGL
                 */
                String message = GLDebugMessageCallback.getMessage(length, msg);
                String output = "from " + getGLDebugSourceString(source) + " type " + getGLDebugTypeString(type) +
                        " (id:x" + Integer.toString(id, 16) + ") severity " + getGLDebugSeverityString(severity) +
                        " : " + message;
                if (output.equals(lastOpenGLError) && lastErrorCount < 255 && OPENGL_REDUCED_ERROR_OUTPUT) {
                    lastErrorCount++;
                } else {
                    if (lastErrorCount > 1) Log.w("openGL", lastErrorCount + " more of the same error");
                    lastOpenGLError = output;
                    lastErrorCount = 1;

                    if (severity == GL43.GL_DEBUG_SEVERITY_NOTIFICATION || severity == GL43.GL_DEBUG_SEVERITY_LOW
                            || severity == GL43.GL_DEBUG_SEVERITY_MEDIUM) {
                        if (id == NV_BUFFER_USE_VRAM) { // NVIDIA drives are quite chatty and provide how they store their VAOs
                            Log.d("openGL", output);
                        } else {
                            Log.i("openGL", output);
                        }
                    } else {
                        Log.w("openGL", output);
                    }
                }

            }, 0);
        } else {
            Log.i(TAG, "openGL version < 4.3, disabling openGL debugs");
        }
        Log.d(TAG, "openGL context created");
    }

    /**
     * gets a formatted string from an openGL debug source code
     *
     * @param source source code
     * @return source string
     */
    private static String getGLDebugSourceString(int source) {
        String sourceStr = "x" + Integer.toString(source, 16);
        if (source == GL43.GL_DEBUG_SOURCE_API) sourceStr = "API(" + sourceStr + ")";
        if (source == GL43.GL_DEBUG_SOURCE_WINDOW_SYSTEM) sourceStr = "WindowSystem(" + sourceStr + ")";
        if (source == GL43.GL_DEBUG_SOURCE_SHADER_COMPILER) sourceStr = "shader compiler(" + sourceStr + ")";
        if (source == GL43.GL_DEBUG_SOURCE_THIRD_PARTY) sourceStr = "third party(" + sourceStr + ")";
        if (source == GL43.GL_DEBUG_SOURCE_APPLICATION) sourceStr = "application(" + sourceStr + ")";
        if (source == GL43.GL_DEBUG_SOURCE_OTHER) sourceStr = "other(" + sourceStr + ")";
        return sourceStr;
    }

    /**
     * gets a formatted string from an openGL debug type code
     *
     * @param type type code
     * @return type string
     */
    private static String getGLDebugTypeString(int type) {
        String typeStr = "x" + Integer.toString(type, 16);
        if (type == GL43.GL_DEBUG_TYPE_ERROR) typeStr = "error (" + typeStr + ")";
        if (type == GL43.GL_DEBUG_TYPE_DEPRECATED_BEHAVIOR) typeStr = "deprecated (" + typeStr + ")";
        if (type == GL43.GL_DEBUG_TYPE_UNDEFINED_BEHAVIOR) typeStr = "undefined (" + typeStr + ")";
        if (type == GL43.GL_DEBUG_TYPE_OTHER) typeStr = "other (" + typeStr + ")";
        if (type == GL43.GL_DEBUG_TYPE_PERFORMANCE) typeStr = "performance (" + typeStr + ")";
        return typeStr;
    }

    /**
     * gets a formatted string from an openGL debug severity code
     *
     * @param severity severity code
     * @return severity string
     */
    private static String getGLDebugSeverityString(int severity) {
        String severityStr = "x" + Integer.toString(severity, 16);
        if (severity == GL43.GL_DEBUG_SEVERITY_HIGH) severityStr = "high(" + severityStr + ")";
        if (severity == GL43.GL_DEBUG_SEVERITY_MEDIUM) severityStr = "medium(" + severityStr + ")";
        if (severity == GL43.GL_DEBUG_SEVERITY_LOW) severityStr = "low(" + severityStr + ")";
        if (severity == GL43.GL_DEBUG_SEVERITY_NOTIFICATION) severityStr = "notification (" + severityStr + ")";
        return severityStr;
    }

    /**
     * creates a GLFW window
     *
     * @param height windows height in pxl
     * @param width  windows width in pxl
     * @param title  windows title
     * @return the created GLFW window
     */
    public GLFWWindow createWindow(int width, int height, String title) throws IllegalStateException {
        long id = GLFW.glfwCreateWindow(width, height, title, MemoryUtil.NULL, MemoryUtil.NULL);
        if (id == MemoryUtil.NULL) {
            throw new IllegalStateException("Window creation failed");
        }
        GLFWWindow window = new GLFWWindow(id, this);
        GLFW.glfwMakeContextCurrent(id);
        try {
            createGLContext();
        } catch (IllegalStateException ex) { // thrown if openGL version is not supported
            Log.e("could not create OpenGL context, version not supported");
            window.destroyUnsafe();
            return null;
        }
        MasterRenderer.setWindow(window);
        ParticleMaster.init(MasterRenderer.getProjectionMatrix());
        GL11.glViewport(0, 0, width, height);
        GL11.glOrtho(0, width, height, 0.0, -1.0, 1.0);
        Log.i(TAG, "Window created with MSAA x" + GL11.glGetInteger(GL_SAMPLES));
        windows.add(window);
        return window;
    }

    /**
     * creates a GLFW window
     *
     * @return the created GLFW window
     */
    public GLFWWindow createWindow() throws IllegalStateException {
        return createWindow(DEFAULT_WIDTH, DEFAULT_HEIGHT);
    }

    /**
     * creates a GLFW window
     *
     * @param height windows height in pxl
     * @param width  windows width in pxl
     * @return the created GLFW window
     */
    public GLFWWindow createWindow(int width, int height) throws IllegalStateException {
        return createWindow(width, height, DEFAULT_TITLE);
    }

    /**
     * cleans up by destroying all windows and unbinding the error callback
     */
    public void cleanUp() {
        int windowCount = windows.size();
        for (GLFWWindow window : windows) {
            window.destroyUnsafe();
        }
        windows.clear();
        if (windowCount > 0) GL11.glDisable(GL43.GL_DEBUG_OUTPUT);
        GLFW.glfwTerminate();
        Log.i(TAG, "cleaned up");
    }

    /**
     * removes window from the windows list, called if window is destroyed by Window.destroy, to prevent destroying it a
     * second time, resulting in a crash
     *
     * @param window window to remove
     */
    void removeWindow(GLFWWindow window) {
        windows.remove(window);
    }
}