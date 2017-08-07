package engine.graphics.cameras;

import org.joml.Vector3f;

/**
   Created by pv42 on 17.06.16.
 */
public abstract class Camera {
    private Vector3f position = new Vector3f(0,5,0);
    private float pitch = 11, yaw = 30, roll;

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
