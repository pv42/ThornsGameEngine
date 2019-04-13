package engine.physics;

import org.joml.Vector3f;

public class RadialHitBox implements HitBox {
    private Vector3f center;
    private float radius;

    public RadialHitBox(Vector3f center, float radius) {
        this.center = center;
        this.radius = radius;
    }

    public Vector3f getCenter() {
        return center;
    }

    public void setCenter(Vector3f center) {
        this.center = center;
    }

    public float getRadius() {
        return radius;
    }

    public void setRadius(float radius) {
        this.radius = radius;
    }
}
