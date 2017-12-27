package engine.physics;

import org.joml.Vector3f;

public interface Physical {
    void setHitBox(HitBox hitBox);
    void setIgnoreCollision(boolean ignoreCollision);
    void setMass(float mass);
    void setStatic(boolean stat);
    boolean isStatic();
    void setVelocity(Vector3f velocity);
    void setIgnoreGravity(boolean ignoreGravity);
    Vector3f getVelocity();
    float getMass();
    boolean isIgnoreGravity();
    boolean ignoresCollision();
    HitBox getHitBox();
    Vector3f getPosition();
    void accelerate(Vector3f acceleration);
    void increasePosition(float dx, float dy, float dz);
    default Vector3f getImpulse() {
        return getVelocity().mul(getMass(), new Vector3f());
    }
}
