package engine.graphics.cameras;

import org.joml.Matrix4f;
import org.joml.Vector3f;

/**
   Created by pv42 on 17.06.16.
 */
public abstract class ThreeDimensionCamera implements Camera{
    private Vector3f position = new Vector3f(0,5,0);
    private float pitch = 11;
    private float yaw = 30;
    private float roll = 0;

    @Override
    public Matrix4f getViewMatrix() {
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

    public abstract void move();

    public Vector3f getPosition() {
        return position;
    }

    public float getPitch() {
        return pitch;
    }

    public float getYaw() {
        return yaw;
    }

    public float getRoll() {
        return roll;
    }

    public void setPosition(Vector3f position) {
        this.position = position;
    }

    public void setPitch(float pitch) {
        this.pitch = pitch;
    }

    public void setYaw(float yaw) {
        this.yaw = yaw;
    }

    public void setRoll(float roll) {
        this.roll = roll;
    }
}
