package engine.physics;

import engine.graphics.entities.Entity;
import engine.graphics.models.TexturedModel;
import org.joml.Vector3f;


public class PhysicalEntity extends Entity {
    private Vector3f velocity = new Vector3f();
    private float mass;
    private boolean ignoreGravity = false;
    private boolean ignoreCollision = false;
    private HitBox hitBox;
    public PhysicalEntity(TexturedModel model, Vector3f position, float mass) {
        super(model, position);
        this.mass = mass;
    }

    public PhysicalEntity(TexturedModel model, int textureIndex, Vector3f position, float mass) {
        super(model, textureIndex, position);
        this.mass = mass;
    }

    public void accelerate(Vector3f acceleration) {
        velocity.add(acceleration);
    }

    public Vector3f getVelocity() {
        return velocity;
    }

    public void setVelocity(Vector3f velocity) {
        this.velocity = velocity;
    }

    public float getMass() {
        return mass;
    }

    public void setMass(float mass) {
        this.mass = mass;
    }

    public boolean isIgnoreGravity() {
        return ignoreGravity;
    }

    public void setIgnoreGravity(boolean ignoreGravity) {
        this.ignoreGravity = ignoreGravity;
    }

    public boolean isIgnoreCollision() {
        return ignoreCollision;
    }

    public void setIgnoreCollision(boolean ignoreCollision) {
        this.ignoreCollision = ignoreCollision;
    }

    public HitBox getHitBox() {
        return hitBox;
    }

    public void setHitBox(HitBox hitBox) {
        this.hitBox = hitBox;
    }
}
