package engine.graphics.renderEngine;

import engine.toolbox.Log;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.*;
import org.lwjgl.system.MemoryUtil;
import org.lwjgl.util.vector.Vector2f;

import static engine.toolbox.Settings.*;
import static engine.toolbox.Time.getTime;

public class DisplayManager {
    private static final String TAG = "Engine:DisplayManager";
    private static final String TITLE ="engine_";
    private static long lastFrameEnd;
    private static float delta;
    private static long windowID;

    /**
     * creates a GLFW window
     *
     * @return windowID
     */
    public static long createDisplay() {
        windowID = GLFW.glfwCreateWindow(WIDTH,HEIGHT,TITLE, MemoryUtil.NULL,MemoryUtil.NULL);
        if(windowID == MemoryUtil.NULL) throw new IllegalStateException("Windows creation failed");
        GLFW.glfwMakeContextCurrent(windowID);
        GLFW.glfwSwapInterval(FPS_LIMIT/60);
        GLFW.glfwShowWindow(windowID);
        GL.createCapabilities();
        printDisplayModes();
        GL11.glViewport(0,0,WIDTH,HEIGHT);
        lastFrameEnd = getTime();
        Log.i(TAG,"Display created");
        return windowID;
    }

    /**
     * updates the windows
     */
    public static void updateDisplay() {
        GLFW.glfwSwapBuffers(windowID);
        GLFW.glfwPollEvents();
        long currentFrameTime = getTime();
        delta = (currentFrameTime - lastFrameEnd)/1000f;
        lastFrameEnd = currentFrameTime;
    }

    /**
     * destroys the GLFW window
     */
    public static void destroyDisplay() {
        GLFW.glfwDestroyWindow(windowID);
        Log.i(TAG,"Display destroyed");
    }

    /**
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
            modes += vidMode.height() + "x" + vidMode.width() + "@" + vidMode.refreshRate()+ "; ";
        }
        Log.d(TAG,"Monitor " + GLFW.glfwGetMonitorName(monitor) + " supports following vidModes:" + modes);
    }
    /**
     * Updates the windows dimension and fullscreen state
     *
     * @return {@code true} if a valid setting was found.
     */
    public static boolean updateDisplayMode(int width, int height, boolean fullscreen) {
        //todo
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
     *
     * @return {@code true} if a close request was send to the GLFW window
     */
    public static boolean isCloseRequested() {
        return GLFW.glfwWindowShouldClose(windowID);
    }

    /**
     *
     * @return the window size as Vector2D
     */
    public static Vector2f getSize() {
        int[] h = new int[1],w = new int[1];
        GLFW.glfwGetWindowSize(windowID,h,w);
        return new Vector2f(w[0],h[0]);
    }

    /**
     * @param b {@code false} enable the courser (default)
     *          {@code true} disable the courser
     */
    public static void setMouseGrabbed(boolean b) {
        if(b) {
            GLFW.glfwSetInputMode(windowID,GLFW.GLFW_CURSOR,GLFW.GLFW_CURSOR_DISABLED);
        } else {
            GLFW.glfwSetInputMode(windowID,GLFW.GLFW_CURSOR,GLFW.GLFW_CURSOR_NORMAL);
        }
    }

}