package engine.graphics.cameras;

import org.joml.Vector3f;

/***
 * Created by pv42 on 20.07.16.
 */
public class StaticCamera extends Camera {
    public StaticCamera(Vector3f position, Vector3f rotations) {
        super();
        setPosition(position);
        setPitch(rotations.x);
        setYaw(rotations.y);
        setRoll(rotations.z);
    }

    @Override
    public void move() {

    }
}
