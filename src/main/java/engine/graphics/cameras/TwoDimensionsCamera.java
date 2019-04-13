package engine.graphics.cameras;

import org.joml.Matrix4f;
import org.joml.Vector2f;

/**
 * a 2d dimensional camera
 *
 * @author pv42
 */
public class TwoDimensionsCamera implements Camera {
    private Vector2f position;

    /**
     * creates a 2d camera at 0,0
     */
    public TwoDimensionsCamera() {
        position = new Vector2f();
    }

    /**
     * creates a 2d camera at a given position
     *
     * @param position camera's position
     */
    public void setPosition(Vector2f position) {
        this.position = position;
    }

    @Override
    public Matrix4f getViewMatrix() {
        Matrix4f viewMatrix = new Matrix4f();
        viewMatrix.identity();
        viewMatrix.translate(-position.x(), -position.y, 0);
        return viewMatrix;
    }
}
