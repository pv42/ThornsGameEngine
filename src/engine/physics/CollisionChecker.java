package engine.physics;

import org.joml.Vector3f;

public class CollisionChecker {
    public static boolean isColliding(HitBox h0, HitBox h1, Vector3f pos0, Vector3f pos1) {
        if(h0 instanceof CuboidHitBox && h1 instanceof CuboidHitBox) {
            float h0x0 = ((CuboidHitBox) h0).getX0() + pos0.x;
            float h0x1 = ((CuboidHitBox) h0).getX1() + pos0.x;
            float h1x0 = ((CuboidHitBox) h1).getX0() + pos1.x;
            float h1x1 = ((CuboidHitBox) h1).getX1() + pos1.x;
            boolean colx = (h0x1 >= h1x0 && h1x1 >= h0x0) || (h1x0 >= h0x1 && h0x0 >= h1x1);
            if(!colx) return false;
            float h0y0 = ((CuboidHitBox) h0).getY0() + pos0.y;
            float h0y1 = ((CuboidHitBox) h0).getY1() + pos0.y;
            float h1y0 = ((CuboidHitBox) h1).getY0() + pos1.y;
            float h1y1 = ((CuboidHitBox) h1).getY1() + pos1.y;
            boolean coly = (h0y1 >= h1y0 && h1y1 >= h0y0) || (h1y0 >= h0y1 && h0y0 >= h1y1);
            if(!coly) return false;
            float h0z0 = ((CuboidHitBox) h0).getZ0() + pos0.z;
            float h0z1 = ((CuboidHitBox) h0).getZ1() + pos0.z;
            float h1z0 = ((CuboidHitBox) h1).getZ0() + pos1.z;
            float h1z1 = ((CuboidHitBox) h1).getZ1() + pos1.z;
            return (h0z1 >= h1z0 && h1z1 >= h0z0) || (h1z0 >= h0z1 && h0z0 >= h1z1);
        } else {
            throw new UnsupportedOperationException("todo");
        }
    }
}
