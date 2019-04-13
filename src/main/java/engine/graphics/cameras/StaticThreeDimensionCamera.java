package engine.graphics.cameras;

import org.joml.Vector3f;

/**
 * A 3d camera that is at a fixed position and rotation
 *
 * @author pv42
 */
public class StaticThreeDimensionCamera extends ThreeDimensionCamera {
    /**
     * creates a 3d static camera at (0,0,0) position without rotation
     */
    public StaticThreeDimensionCamera() {
        super();
    }

    /**
     * creates a 3d static camera at a given position without rotation
     *
     * @param position cameras position
     */
    public StaticThreeDimensionCamera(Vector3f position) {
        super();
        setPosition(position);
    }

    /**
     * creates a 3d static camera at a given position and rotation in pitch, yaw and roll
     *
     * @param position  cameras position
     * @param rotations cameras pitch, yaw and roll as a vector
     */
    public StaticThreeDimensionCamera(Vector3f position, Vector3f rotations) {
        super();
        setPosition(position);
        setPitch(rotations.x);
        setYaw(rotations.y);
        setRoll(rotations.z);
    }

    /**
     * does not move or do anything at all
     */
    @Override
    public void move() {

    }
}
