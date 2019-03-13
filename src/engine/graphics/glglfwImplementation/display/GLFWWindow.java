package engine.graphics.glglfwImplementation.display;


import engine.graphics.display.Window;
import engine.toolbox.Log;
import org.joml.Vector2i;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWWindowSizeCallback;
import org.lwjgl.opengl.GL11;

import static engine.toolbox.Time.getNanoTime;


//

/**
 * A GLFW window Object
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
     * creates window object from a GLFW id
     *
     * @param id          windows id (from glfw)
     * @param initialSize windows size
     * @param manger      display manager who created this window
     */
    GLFWWindow(long id, Vector2i initialSize, GLFWDisplayManager manger) {
        this.id = id;
        size = initialSize;
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
     * shows the window
     */
    public void show() {
        GLFW.glfwShowWindow(id);
    }

    /**
     * hide the window
     */
    public void hide() {
        GLFW.glfwHideWindow(id);
    }

    /**
     * destroys the window and removes it from the display managers window list
     */
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

    /**
     * gets the windows aspect ratio, calculated from width/height
     *
     * @return the windows aspect ration
     */
    public float getAspectRatio() {
        return (float) size.x() / (float) size.y();
    }

    /**
     * gets the windows size
     *
     * @return windows size with x is width and y is height
     */
    public Vector2i getSize() {
        return size;
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
     * Sets the windows size
     *
     * @param width  windows width to set
     * @param height windows height to set
     */
    public void setSize(int width, int height) {
        size.set(width, height);
        GLFW.glfwSetWindowSize(id, width, height);
    }

    /**
     * Sets the windows position, this is aligned to the top left corner of the render area ignoring the windows padding
     *
     * @param x windows x position to set
     * @param y windows y position to set
     */
    public void setPosition(int x, int y) {
        GLFW.glfwSetWindowPos(id, x, y);
    }

    /**
     * destroys window, do not use outside of destroy() and GLFWDisplayManger.cleanUp()
     */
    void destroyUnsafe() {
        GLFW.glfwDestroyWindow(id);
    }

    /**
     * returns the time between the last 2 frames displayed in seconds
     *
     * @return the time between the last 2 frames displayed in seconds
     */
    public float getLastFrameTime() {
        return frameDelta;
    }

    /**
     * lets the window grab or release the mouse
     * @param b whether to grab or to release the mouse
     * @param window window the grab the mouse in
     */
    public void setMouseGrabbed(boolean b, GLFWWindow window) {
        if(b) {
            GLFW.glfwSetInputMode(window.getId(),GLFW.GLFW_CURSOR,GLFW.GLFW_CURSOR_DISABLED);
        } else {
            GLFW.glfwSetInputMode(window.getId(),GLFW.GLFW_CURSOR,GLFW.GLFW_CURSOR_NORMAL);
        }
    }

    public void setTitle(String title) {
        GLFW.glfwSetWindowTitle(id, title);
    }
}

