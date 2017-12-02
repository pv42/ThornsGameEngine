package engine.physics;

import engine.graphics.entities.Entity;
import engine.graphics.models.TexturedModel;
import org.joml.Vector3f;

public class PhysicalEntity extends Entity implements Physical {
    private Vector3f velocity = new Vector3f();
    private float mass;
    private boolean ignoreGravity = false;
    private boolean ignoreCollision = false;
    private HitBox hitBox;

    //<init>
    public PhysicalEntity(TexturedModel model, Vector3f position, float mass) {
        super(model, position);
        this.mass = mass;
    }

    public PhysicalEntity(TexturedModel model, int textureIndex, Vector3f position, float mass) {
        super(model, textureIndex, position);
        this.mass = mass;
    }

    //setters
    public void setVelocity(Vector3f velocity) {
        this.velocity = velocity;
    }

    public void setMass(float mass) {
        this.mass = mass;
    }

    public void setIgnoreGravity(boolean ignoreGravity) {
        this.ignoreGravity = ignoreGravity;
    }

    public void setIgnoreCollision(boolean ignoreCollision) {
        this.ignoreCollision = ignoreCollision;
    }

    public void setHitBox(HitBox hitBox) {
        this.hitBox = hitBox;
    }

    //getters
    public Vector3f getVelocity() {
        return velocity;
    }

    public float getMass() {
        return mass;
    }

    public boolean isIgnoreGravity() {
        return ignoreGravity;
    }

    public boolean ignoresCollision() {
        return ignoreCollision;
    }

    public HitBox getHitBox() {
        return hitBox;
    }

    //other methods
    public void accelerate(Vector3f acceleration) {
        velocity.add(acceleration);
    }
}
