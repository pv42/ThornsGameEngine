package engine.graphics.cameras;

import org.joml.Matrix4f;
import org.joml.Vector3f;

/**
 * Created by pv42 on 17.06.16.
 */
public abstract class ThreeDimensionCamera implements Camera {
    private Vector3f position = new Vector3f(0, 5, 0);
    private float pitch = 11;
    private float yaw = 30;
    private float roll = 0;

    @Override
    public final Matrix4f getViewMatrix() {
        Matrix4f viewMatrix = new Matrix4f();
        viewMatrix.identity();
        viewMatrix.rotate((float) Math.toRadians(pitch), new Vector3f(1, 0, 0));
        viewMatrix.rotate((float) Math.toRadians(yaw), new Vector3f(0, 1, 0));
        viewMatrix.rotate((float) Math.toRadians(roll), new Vector3f(0, 0, 1));
        Vector3f negativeCameraPos = new Vector3f();
        position.negate(negativeCameraPos);
        viewMatrix.translate(negativeCameraPos);
        return viewMatrix;
    }


    /**
     * cameras tick method, does nothing per default
     */
    public void move() {

    }

    /**
     * gets cameras position in world space
     *
     * @return cameras position
     */
    public final Vector3f getPosition() {
        return position;
    }

    public void setPosition(Vector3f position) {
        this.position = position;
    }

    /**
     * gets the camera's rotation pitch
     *
     * @return pitch
     */
    public final float getPitch() {
        return pitch;
    }

    /**
     * sets the camera's rotation pitch
     *
     * @param pitch rotation pitch
     */
    public void setPitch(float pitch) {
        this.pitch = pitch;
    }

    /**
     * gets the camera's rotation yaw
     *
     * @return yaw
     */
    public float getYaw() {
        return yaw;
    }

    /**
     * sets the camera's rotation yaw
     *
     * @param yaw rotation yaw
     */
    public void setYaw(float yaw) {
        this.yaw = yaw;
    }

    /**
     * sets the camera's rotation roll
     *
     * @return roll
     */
    public float getRoll() {
        return roll;
    }

    /**
     * gets the camera's rotation roll
     *
     * @param roll rotation roll
     */
    public void setRoll(float roll) {
        this.roll = roll;
    }
}
