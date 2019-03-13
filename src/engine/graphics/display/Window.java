package engine.graphics.display;

import org.joml.Vector2ic;

/**
 * A window object's minimum functionality
 *
 * @author pv42
 */
public interface Window {
    void show();

    void hide();

    void destroy();

    void makeActive();

    boolean isCloseRequested();

    void update();

    float getAspectRatio();

    Vector2ic getSize();

    void setSize(int width, int height);

    void setTitle(String title);

    void setPosition(int x, int y);

    /**
     * returns the time between the last 2 frames displayed in seconds
     *
     * @return the time between the last 2 frames displayed in seconds
     */
    float getLastFrameTime();

}
