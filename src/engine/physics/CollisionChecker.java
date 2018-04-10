package engine.physics;

import engine.toolbox.Log;
import org.joml.Vector3f;

class CollisionChecker {
    static boolean isColliding(Physical ht0, Physical ht1, Vector3f pos0, Vector3f pos1) {
        HitBox h0 = ht0.getHitBox();
        HitBox h1 = ht1.getHitBox();
        if (h0 instanceof CuboidHitBox && h1 instanceof CuboidHitBox) {
            return isCollidingCC((CuboidHitBox) h0, (CuboidHitBox) h1, pos0, pos1);
        } else if (h0 instanceof RadialHitBox && h1 instanceof CuboidHitBox) {
            return isCollidingCR((CuboidHitBox) h1, (RadialHitBox) h0, pos1, pos0);
        } else if (h1 instanceof RadialHitBox && h0 instanceof CuboidHitBox) {
            return isCollidingCR((CuboidHitBox) h0, (RadialHitBox) h1, pos0, pos1);
        } else if (h0 instanceof RadialHitBox && h1 instanceof RadialHitBox) {
            return isCollidingRR((RadialHitBox) h0, (RadialHitBox) h1, pos0, pos1);
        } else {
            throw new UnsupportedOperationException("todo");
        }
    }

    private static boolean isCollidingCR(CuboidHitBox h0, RadialHitBox h1, Vector3f pos0, Vector3f pos1) {
        Log.w("Collision", "sphere approximated as cube");
        float h0x0 = h0.getX0() + pos0.x;
        float h0x1 = h0.getX1() + pos0.x;
        float h1x0 = h1.getCenter().x-h1.getRadius() + pos1.x;
        float h1x1 = h1.getCenter().x+h1.getRadius() + pos1.x;
        boolean colx = (h0x1 >= h1x0 && h1x1 >= h0x0) || (h1x0 >= h0x1 && h0x0 >= h1x1);
        if(!colx) return false;
        float h0y0 = h0.getY0() + pos0.y;
        float h0y1 = h0.getY1() + pos0.y;
        float h1y0 = h1.getCenter().y-h1.getRadius() + pos1.y;
        float h1y1 = h1.getCenter().y+h1.getRadius() + pos1.y;
        boolean coly = (h0y1 >= h1y0 && h1y1 >= h0y0) || (h1y0 >= h0y1 && h0y0 >= h1y1);
        if(!coly) return false;
        float h0z0 = h0.getZ0() + pos0.z;
        float h0z1 = h0.getZ1() + pos0.z;
        float h1z0 = h1.getCenter().z-h1.getRadius() + pos1.z;
        float h1z1 = h1.getCenter().z+h1.getRadius() + pos1.z;
        return (h0z1 >= h1z0 && h1z1 >= h0z0) || (h1z0 >= h0z1 && h0z0 >= h1z1);
    }

    private static boolean isCollidingCC(CuboidHitBox h0, CuboidHitBox h1, Vector3f pos0, Vector3f pos1) {
        float h0x0 = h0.getX0() + pos0.x;
        float h0x1 = h0.getX1() + pos0.x;
        float h1x0 = h1.getX0() + pos1.x;
        float h1x1 = h1.getX1() + pos1.x;
        boolean colx = (h0x1 >= h1x0 && h1x1 >= h0x0) || (h1x0 >= h0x1 && h0x0 >= h1x1);
        if(!colx) return false;
        float h0y0 = h0.getY0() + pos0.y;
        float h0y1 = h0.getY1() + pos0.y;
        float h1y0 = h1.getY0() + pos1.y;
        float h1y1 = h1.getY1() + pos1.y;
        boolean coly = (h0y1 >= h1y0 && h1y1 >= h0y0) || (h1y0 >= h0y1 && h0y0 >= h1y1);
        if(!coly) return false;
        float h0z0 = h0.getZ0() + pos0.z;
        float h0z1 = h0.getZ1() + pos0.z;
        float h1z0 = h1.getZ0() + pos1.z;
        float h1z1 = h1.getZ1() + pos1.z;
        return (h0z1 >= h1z0 && h1z1 >= h0z0) || (h1z0 >= h0z1 && h0z0 >= h1z1);
    }

    private static boolean isCollidingRR(RadialHitBox h0, RadialHitBox h1, Vector3f pos0, Vector3f pos1) {
        Vector3f p0 = h0.getCenter().add(pos0, new Vector3f());
        Vector3f p1 = h1.getCenter().add(pos1, new Vector3f());
        Vector3f dp = p0.sub(p1);
        return dp.lengthSquared() < (h0.getRadius() + h1.getRadius()) * (h0.getRadius() + h1.getRadius());
    }
}
