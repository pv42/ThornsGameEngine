package engine.graphics.display;

import org.joml.Vector2ic;

/**
 * A window object's minimum functionality
 *
 * @author pv42
 */
public interface Window {
    /**
     * shows the window
     */
    void show();

    /**
     * hide the window
     */
    void hide();

    /**
     * destroys the window and removes it from the display managers window list
     */
    void destroy();

    void makeActive();

    boolean isCloseRequested();

    void update();

    /**
     * gets the windows aspect ratio, calculated from width/height
     *
     * @return the windows aspect ration
     */
    float getAspectRatio();

    /**
     * gets the windows size
     *
     * @return windows size with x is width and y is height
     */
    Vector2ic getSize();

    /**
     * Sets the windows size
     *
     * @param width  windows width to set
     * @param height windows height to set
     */
    void setSize(int width, int height);

    /**
     * sets the windows title
     * @param title string to make windows title
     */
    void setTitle(String title);

    /**
     * Sets the windows position, this is aligned to the top left corner of the render area ignoring the windows padding
     *
     * @param x windows x position to set
     * @param y windows y position to set
     */
    void setPosition(int x, int y);

    /**
     * returns the time between the last 2 frames displayed in seconds
     *
     * @return the time between the last 2 frames displayed in seconds
     */
    float getLastFrameTime();

}
