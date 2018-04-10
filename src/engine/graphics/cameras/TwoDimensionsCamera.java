package engine.graphics.cameras;

import org.joml.Matrix4f;
import org.joml.Vector2f;

public class TwoDimensionsCamera implements Camera {
    private Vector2f position;

    public TwoDimensionsCamera() {
        position = new Vector2f();
    }

    public void setPosition(Vector2f position) {
        this.position = position;
    }

    @Override
    public Matrix4f getViewMatrix() {
        Matrix4f viewMatrix = new Matrix4f();
        viewMatrix.identity();
        viewMatrix.translate(-position.x(), -position.y,0);
        return viewMatrix;
    }
}
