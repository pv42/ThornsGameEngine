package engine.physics;

public class CuboidHitBox implements HitBox{
    private float x0,x1,y0,y1,z0,z2;

    public CuboidHitBox(float x0, float x1, float y0, float y1, float z0, float z2) {
        this.x0 = x0;
        this.x1 = x1;
        this.y0 = y0;
        this.y1 = y1;
        this.z0 = z0;
        this.z2 = z2;
    }

    public float getX0() {
        return x0;
    }

    public float getX1() {
        return x1;
    }

    public float getY0() {
        return y0;
    }

    public float getY1() {
        return y1;
    }

    public float getZ0() {
        return z0;
    }

    public float getZ2() {
        return z2;
    }
}
