package engine.graphics.display;

/**
 * The manager handling the windows and render contexts
 *
 * @author pv42
 */
public interface DisplayManager {
    /**
     * creates a GLFW window
     *
     * @param height windows height in pxl
     * @param width  windows width in pxl
     * @param title  windows title
     * @return the created GLFW window
     */
    Window createWindow(int width, int height, String title) throws IllegalStateException;

    /**
     * creates a window
     *
     * @return the created GLFW window
     */
    Window createWindow() throws IllegalStateException;

    /**
     * creates a window
     *
     * @param height windows height in pxl
     * @param width  windows width in pxl
     * @return the created GLFW window
     */
    Window createWindow(int width, int height) throws IllegalStateException;

    /**
     * cleans up by destroying all windows and unbinding the error callback
     */
    void cleanUp();
}
