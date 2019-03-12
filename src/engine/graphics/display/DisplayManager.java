package engine.graphics.display;

import engine.toolbox.Log;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWVidMode;

import static engine.toolbox.Time.getNanoTime;

public class DisplayManager {
    private static final String TAG = "Engine:DisplayManager";
    private static long lastFrameEnd;
    private static float delta;
    private static Window activeWindow;

    /**
     * creates a GLFW window
     *
     * @return windowID
     */
    public static Window createWindow() {
        Window window = Window.createWindow();
        window.show();
        lastFrameEnd = getNanoTime();
        Log.i(TAG,"Window created");
        Log.d(TAG, "windowId=" + window.getId());
        activeWindow = window;
        return window;
    }

    /**
     * creates the GLFW context
     * */
    public static void init() {
        GLFWErrorCallback.createPrint(System.err).set();
        if(!GLFW.glfwInit()) throw new IllegalStateException("GLFW init failed");
    }

    /**
     * updates the windows
     */
    public static void updateDisplay(Window window) {
        GLFW.glfwSwapBuffers(window.getId());
        GLFW.glfwPollEvents();
        long currentFrameTime = getNanoTime();
        delta = (currentFrameTime - lastFrameEnd)/1000000000f;
        lastFrameEnd = currentFrameTime;
    }

    /**
     * destroys the GLFW context
     */
    public static void destroy() {
        GLFW.glfwTerminate();
        GLFW.glfwSetErrorCallback(null).free();
        Log.i(TAG,"Display destroyed");
        activeWindow = null;
    }

    /**
     * gets the time between the last two display updates
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
     * @param b {@code false} enable the courser (default)
     *          {@code true} disable the courser
     */
    public static void setMouseGrabbed(boolean b, Window window) {
        if(b) {
            GLFW.glfwSetInputMode(window.getId(),GLFW.GLFW_CURSOR,GLFW.GLFW_CURSOR_DISABLED);
        } else {
            GLFW.glfwSetInputMode(window.getId(),GLFW.GLFW_CURSOR,GLFW.GLFW_CURSOR_NORMAL);
        }
    }

    public static Window getActiveWindow() {
        return activeWindow;
    }
}