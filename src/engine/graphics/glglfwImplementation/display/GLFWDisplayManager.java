package engine.graphics.display;

import engine.toolbox.Log;
import engine.toolbox.Settings;
import org.joml.Vector2i;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.GL43;
import org.lwjgl.opengl.GLCapabilities;
import org.lwjgl.opengl.GLDebugMessageCallback;
import org.lwjgl.system.MemoryUtil;

import java.util.ArrayList;
import java.util.List;

import static engine.toolbox.Settings.MSAA;
import static engine.toolbox.Time.getNanoTime;
import static org.lwjgl.glfw.GLFW.GLFW_SAMPLES;
import static org.lwjgl.opengl.GL13.GL_SAMPLES;

/**
 * Manages GLFW contexts and windows
 *
 * @author pv42
 */
public class GLFWDisplayManager {
    private static final String TAG = "Engine:DisplayManager";
    private static final String DEFAULT_TITLE = "Thorns";
    private static int NV_BUFFER_USE_VRAM = 0x20071; // nVidia driver's OpenGL debug id for using vram for vaos
    private static long lastFrameEnd;
    private static float delta;
    private static List<GLFWWindow> windows = new ArrayList<>();
    private static GLFWWindow activeWindow;

    /**
     * initialize GLFW, setting up the error callback and some settings
     */
    public GLFWDisplayManager() {
        GLFWErrorCallback.createPrint(System.err).set();
        if (!GLFW.glfwInit()) {
            if (!GLFW.glfwInit()) throw new IllegalStateException("GLFW init failed");
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
                if (id == NV_BUFFER_USE_VRAM) { // NVIDIA drives are quite chatty and provide how they store their VAOs
                    Log.d("openGL", "from " + getGLDebugSourceString(source) + " in " + getGLDebugTypeString(type) +
                            " (id:x" + Integer.toString(id, 16) + ") severity " + getGLDebugSeverityString(severity) +
                            " : " + message);
                } else {
                    Log.w("openGL", "from " + getGLDebugSourceString(source) + " in " + getGLDebugTypeString(type) +
                            " (id:x" + Integer.toString(id, 16) + ") severity " + getGLDebugSeverityString(severity) +
                            " : " + message);
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
        if (type == GL43.GL_DEBUG_TYPE_ERROR) typeStr = "Error (" + typeStr + ")";
        if (type == GL43.GL_DEBUG_TYPE_DEPRECATED_BEHAVIOR) typeStr = "deprecated (" + typeStr + ")";
        if (type == GL43.GL_DEBUG_TYPE_UNDEFINED_BEHAVIOR) typeStr = "undefined (" + typeStr + ")";
        if (type == GL43.GL_DEBUG_TYPE_OTHER) typeStr = "Other (" + typeStr + ")";
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
        if (severity == GL43.GL_DEBUG_SEVERITY_NOTIFICATION) severityStr = "high (" + severityStr + ")";
        return severityStr;
    }

    /**
     * updates the windows
     */
    public static void updateDisplay(GLFWWindow window) {
        GLFW.glfwSwapBuffers(window.getId());
        GLFW.glfwPollEvents();
        long currentFrameTime = getNanoTime();
        delta = (currentFrameTime - lastFrameEnd) / 1000000000f;
        lastFrameEnd = currentFrameTime;
    }

    /**
     * destroys the GLFW context
     */
    public static void destroy() {
        GLFW.glfwTerminate();
        GLFW.glfwSetErrorCallback(null).free();
        Log.i(TAG, "Display destroyed");
        activeWindow = null;
    }

    /**
     * gets the time between the last two display updates
     *
     * @return time between the last two updateDisplay() calls in s
     */
    public static float getFrameTimeSeconds() {
        return delta;
    }

    @Deprecated
    public static void printDisplayModes() {
        long monitor = GLFW.glfwGetPrimaryMonitor();
        GLFWVidMode.Buffer vmodes = GLFW.glfwGetVideoModes(monitor);
        String modes = "";
        for (int i = 0; i < vmodes.limit(); i++) {
            GLFWVidMode vidMode = vmodes.get(i);
            modes += vidMode.height() + "x" + vidMode.width() + "@" + vidMode.refreshRate() + "; ";
        }
        Log.d(TAG, "Monitor " + GLFW.glfwGetMonitorName(monitor) + " supports following vidModes:" + modes);
    }

    /**
     * Updates the windows dimension and fullscreen state
     *
     * @return {@code true} if a valid setting was found.
     */
    public static boolean updateDisplayMode(int width, int height, boolean fullscreen) {
        //todo implement
        /*if (Display.getDisplayMode().getWidth() == width &&
                Display.getDisplayMode().getHeight() == height &&
                Display.isFullscreen() == fullscreen) return true;
        try {
            DisplayMode targetDM = null;
            if(fullscreen) {
                DisplayMode[] modes = Display.getAvailableDisplayModes();
                int freq = 0;
                for (int i = 0; i < modes.length; i++) {
                    DisplayMode currentDM = modes[i];
                    if (currentDM.getWidth() == width && currentDM.getHeight() == height) {
                        if (targetDM == null || currentDM.getFrequency() >= freq) {
                            if (targetDM == null || currentDM.getBitsPerPixel() >= targetDM.getBitsPerPixel()) {
                                targetDM = currentDM;
                                freq = currentDM.getFrequency();
                            }
                        }
                        if (currentDM.getBitsPerPixel() == Display.getDesktopDisplayMode().getBitsPerPixel() &&
                                currentDM.getFrequency() == Display.getDesktopDisplayMode().getFrequency()) {
                            targetDM = currentDM;
                            break;
                        }
                    }
                }
            } else {
                targetDM = new DisplayMode(width,height);
            }
            if(targetDM == null) {
                Log.w(TAG,"failed to find value mode: " + width + "x" + height  + ", fs=" + fullscreen);
                return false;
            }
            Display.setDisplayMode(targetDM);
            Display.setFullscreen(fullscreen);
            Display.setTitle(TITLE);
            if(MULTI_SAMPLE_ANTI_ALIASING > 1) GL11.glEnable(GL13.GL_MULTISAMPLE);
        }catch (LWJGLException e) {
            Log.e(TAG,"unable to  setup displaymode:");
            e.printStackTrace();
            return false;
        }
        Log.i(TAG,String.format("Display updated: %dx%d fs=%s", width, height, fullscreen));*/
        return true;
    }

    /**
     * (un-)grabbes the mouse
     *
     * @param b {@code false} enable the courser (default)
     *          {@code true} disable the courser
     */
    public static void setMouseGrabbed(boolean b, GLFWWindow window) {
        if (b) {
            GLFW.glfwSetInputMode(window.getId(), GLFW.GLFW_CURSOR, GLFW.GLFW_CURSOR_DISABLED);
        } else {
            GLFW.glfwSetInputMode(window.getId(), GLFW.GLFW_CURSOR, GLFW.GLFW_CURSOR_NORMAL);
        }
    }

    public static GLFWWindow getActiveWindow() {
        return activeWindow;
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
            throw new IllegalStateException("GLFWWindow creation failed");
        }
        GLFWWindow window = new GLFWWindow(id, new Vector2i(width, height), this);
        GLFW.glfwMakeContextCurrent(id);
        try {
            createGLContext();
        } catch (IllegalStateException ex) { // thrown if openGL version is not supported
            Log.e("could not create OpenGL context, version not supported");
            window.destroyUnsafe();
            return null;
        }
        GL11.glViewport(0, 0, width, height);
        GL11.glOrtho(0, width, height, 0.0, -1.0, 1.0);
        Log.i(TAG, "GLFWWindow created with MSAA x" + GL11.glGetInteger(GL_SAMPLES));
        windows.add(window);
        return window;
    }

    /**
     * creates a GLFW window
     *
     * @return the created GLFW window
     */
    public GLFWWindow createWindow() throws IllegalStateException {
        return createWindow(Settings.WIDTH, Settings.HEIGHT);
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


}