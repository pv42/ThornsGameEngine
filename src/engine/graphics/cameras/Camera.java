package engine.graphics.cameras;

import org.joml.Matrix4f;

/**
 * A camera is a setting on how to observe the scene, it creates a view matrix witch transforms world to view space
 */
public interface Camera {
    /**
     *
     * @return the view matrix wich converts world space coordinates to view space coordinates
     */
    Matrix4f getViewMatrix();
}
