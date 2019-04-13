package engine.physics;

import engine.graphics.glglfwImplementation.entities.GLEntity;
import engine.graphics.glglfwImplementation.models.GLMaterializedModel;
import org.joml.Vector3f;

import static engine.physics.PhysicsEngine.COLLISION_TYPE_ELASTIC;

public class PhysicalEntity extends GLEntity implements Physical {
    private Vector3f velocity = new Vector3f();
    private float mass;
    private boolean ignoreGravity = false;
    private boolean ignoreCollision = false;
    private HitBox hitBox;
    private boolean isStatic;
    private int collisionType = COLLISION_TYPE_ELASTIC;

    //<init>
    public PhysicalEntity(GLMaterializedModel model, Vector3f position, float mass) {
        super(model, position);
        this.mass = mass;
    }

    public PhysicalEntity(GLMaterializedModel model, int textureIndex, Vector3f position, float mass) {
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

    public void setStatic(boolean stat) {
        this.isStatic = stat;
    }
    //getters
    public Vector3f getVelocity() {
        return velocity;
    }

    public float getMass() {
        return mass;
    }

    public boolean isIgnoreGravity() {
        return ignoreGravity || isStatic;
    }

    public boolean ignoresCollision() {
        return ignoreCollision;
    }

    public HitBox getHitBox() {
        return hitBox;
    }

    public boolean isStatic() {
        return isStatic;
    }

    //other methods
    public void accelerate(Vector3f acceleration) {
        velocity.add(acceleration);
    }

    @Override
    public int getCollisionType() {
        return collisionType;
    }

    public void setCollisionType(int collisionType) {
        this.collisionType = collisionType;
    }
}
