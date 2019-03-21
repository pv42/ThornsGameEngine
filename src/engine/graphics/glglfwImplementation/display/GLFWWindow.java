package engine.graphics.glglfwImplementation.display;

import engine.graphics.display.Window;
import org.joml.Vector2i;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWWindowSizeCallback;
import org.lwjgl.opengl.GL11;

import static engine.toolbox.Time.getNanoTime;

/**
 * A GLFW window
 *
 * @author pv42
 */
public class GLFWWindow implements Window {

    private final long id;
    private Vector2i size;
    private GLFWDisplayManager manger;
    private float frameDelta = 0;
    private long lastFrameTime;

    /**
     * creates window object from a GLFW id, should only be called from GLFWDisplayManager
     *
     * @param id     windows id (from glfw)
     * @param manger display manager who created this window
     */
    GLFWWindow(long id, GLFWDisplayManager manger) {
        this.id = id;
        int[] w = new int[1];
        int[] h = new int[1];
        GLFW.glfwGetWindowSize(id, w, h);
        size = new Vector2i(w[0], h[0]);
        GLFW.glfwSetWindowSizeCallback(id, new GLFWWindowSizeCallback() {
            @Override
            public void invoke(long window, int width, int height) {
                size = new Vector2i(width, height);
                GL11.glViewport(0, 0, width, height);
                GL11.glOrtho(0, width, height, 0.0, -1.0, 1.0);
            }
        });
        this.manger = manger;
        lastFrameTime = getNanoTime();
    }

    /**
     * returns the windows GLWF id
     *
     * @return the windows GLFW id
     */
    public long getId() {
        return id;
    }

    /**
     * Sets if vertical synchronisation should be use for the rendering, if enabled caps the framerate to the monitors
     * frame rate.
     *
     * @param enable indicates if vsync should be enabled
     */
    public void setVSync(boolean enable) {
        if (enable) {
            GLFW.glfwSwapInterval(1);
        } else {
            GLFW.glfwSwapInterval(0);
        }
    }

    /**
     * lets the window grab or release the mouse
     *
     * @param b      whether to grab or to release the mouse
     * @param window window the grab the mouse in
     */
    public void setMouseGrabbed(boolean b, GLFWWindow window) {
        if (b) {
            GLFW.glfwSetInputMode(window.getId(), GLFW.GLFW_CURSOR, GLFW.GLFW_CURSOR_DISABLED);
        } else {
            GLFW.glfwSetInputMode(window.getId(), GLFW.GLFW_CURSOR, GLFW.GLFW_CURSOR_NORMAL);
        }
    }

    /**
     * destroys window, do not use outside of destroy() and GLFWDisplayManger.cleanUp()
     */
    void destroyUnsafe() {
        GLFW.glfwDestroyWindow(id);
    }


    public void show() {
        GLFW.glfwShowWindow(id);
    }

    public void hide() {
        GLFW.glfwHideWindow(id);
    }

    public void destroy() {
        destroyUnsafe();
        manger.removeWindow(this);
    }

    /**
     * make window current render target
     */
    public void makeActive() {
        GLFW.glfwMakeContextCurrent(id);
    }

    /**
     * returns if windows close is requested by the os e.g. ALT+F4
     *
     * @return true if the close is requested, false otherwise
     */
    public boolean isCloseRequested() {
        return GLFW.glfwWindowShouldClose(id);
    }

    /**
     * updates the window by swapping the openGL buffers, poll input events
     */
    public void update() {
        GLFW.glfwSwapBuffers(id);
        GLFW.glfwPollEvents();
        long currentFrameTime = getNanoTime();
        frameDelta = (currentFrameTime - lastFrameTime) / 1e9f;
        lastFrameTime = currentFrameTime;
    }


    public float getAspectRatio() {
        return (float) size.x() / (float) size.y();
    }

    public Vector2i getSize() {
        return size;
    }

    public void setSize(int width, int height) {
        size.set(width, height);
        GLFW.glfwSetWindowSize(id, width, height);
    }

    public void setPosition(int x, int y) {
        GLFW.glfwSetWindowPos(id, x, y);
    }

    /**
     * returns the time between the last 2 frames displayed in seconds
     *
     * @return the time between the last 2 frames displayed in seconds
     */
    public float getLastFrameTime() {
        return frameDelta;
    }

    public void setTitle(String title) {
        GLFW.glfwSetWindowTitle(id, title);
    }
}

