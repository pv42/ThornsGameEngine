package engine.physics;

import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;

import static engine.toolbox.Settings.GRAVITY;

public class PhysicsEngine {
    private static List<PhysicalEntity> entities = new ArrayList<>();
    public static void performStep(float timestep) {
        for(PhysicalEntity entity: entities) {
            if(!entity.isIgnoreGravity()) entity.accelerate(new Vector3f(0, - GRAVITY, 0));
            entity.increasePosition(entity.getVelocity().x * timestep,
                    entity.getVelocity().y * timestep, entity.getVelocity().z * timestep);
        }
    }
}
