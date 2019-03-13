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

    void setPosition(int x, int y);

}
