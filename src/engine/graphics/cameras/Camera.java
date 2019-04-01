package engine.graphics.cameras;

import org.joml.Matrix4f;

/**
 * A camera is a setting on how to observe the scene, it creates a view matrix witch transforms world to view space
 */
public interface Camera {
    /**
     * calculates and returns the cameras view matrix, which converts world space coordinates to view space coordinates,
     * based on the cameras parameters, e.g. position, rotation
     *
     * @return the calculated view matrix
     */
    Matrix4f getViewMatrix();
}
