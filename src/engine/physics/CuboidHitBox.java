package engine.physics;

public class CuboidHitBox implements HitBox{
    private float x0,x1,y0,y1,z0,z1;

    public CuboidHitBox(float x0, float x1, float y0, float y1, float z0, float z1) {
        if(x1>x0) {
            this.x0 = x0;
            this.x1 = x1;
        } else {
            this.x0 = x1;
            this.x1 = x0;
        }
        if(y1>y0) {
            this.y0 = y0;
            this.y1 = y1;
        } else {
            this.y0 = y1;
            this.y1 = y0;
        }
        if(z1>z0) {
            this.z0 = z0;
            this.z1 = z1;
        } else {
            this.z0 = z1;
            this.z1 = z0;
        }
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

    public float getZ1() {
        return z1;
    }
}
