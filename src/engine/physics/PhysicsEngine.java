package engine.physics;

import engine.toolbox.Log;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;

import static engine.toolbox.Settings.GRAVITY;

public class PhysicsEngine {
    private static final String TAG ="PhysicsEngine";
    private static List<PhysicalEntity> entities = new ArrayList<>();
    public static void performStep(float timestep) {
        for(PhysicalEntity entity: entities) {
            if(!entity.isIgnoreGravity()) entity.accelerate(new Vector3f(0, - GRAVITY * timestep, 0));
            entity.increasePosition(entity.getVelocity().x * timestep,
                    entity.getVelocity().y * timestep, entity.getVelocity().z * timestep);
        }
        for(int i = 0; i < entities.size(); i++) {
            for(int j = i + 1; j < entities.size(); j++) {
                // if the objects are clipping change velocity
                PhysicalEntity e0 = entities.get(i);
                PhysicalEntity e1 = entities.get(j);
                if(CollisionChecker.isColliding(e0.getHitBox(), e1.getHitBox(), e0.getPosition(), e1.getPosition())) {
                    Vector3f oldv0 = new Vector3f(e0.getVelocity());
                    Vector3f oldv1 = new Vector3f(e1.getVelocity());
                    Vector3f u = e0.getVelocity().mul(e0.getMass(), new Vector3f()).add(e1.getVelocity().mul(e1.getMass(), new Vector3f()))
                            .mul(2/(e0.getMass()+ e1.getMass()));
                    Log.i("Collision detected between");
                    u.sub(e0.getVelocity(), e0.getVelocity());
                    u.sub(e1.getVelocity(), e1.getVelocity());
                    Log.i("e0:" + oldv0 + " -> " + e0.getVelocity());
                    Log.i("e1:" + oldv1 + " -> " + e1.getVelocity());
                }
            }
        }
    }

    public static void addPhysicalEntity(PhysicalEntity entity) {
        entities.add(entity);
    }
}
