package engine.physics;

import engine.toolbox.Log;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;

import static engine.toolbox.Settings.GRAVITY;

public class PhysicsEngine {
    private static final String TAG ="PhysicsEngine";
    private static List<Physical> physicals = new ArrayList<>();
    public static void performStep(float timeStep) {
        for(Physical physical: physicals) { //applies gravity and velocity
            if(!physical.isIgnoreGravity()) physical.accelerate(new Vector3f(0, - GRAVITY * timeStep, 0));
            physical.increasePosition(physical.getVelocity().x * timeStep,
                    physical.getVelocity().y * timeStep, physical.getVelocity().z * timeStep);
        }
        handleCollisions();
    }
    private static void handleCollisions() {
        for(int i = 0; i < physicals.size(); i++) {
            Physical physical0 = physicals.get(i);
            if(physical0.ignoresCollision()) continue;
            for(int j = i + 1; j < physicals.size(); j++) {
                Physical physical1 = physicals.get(j);
                if(physical1.ignoresCollision()) continue;
                if(CollisionChecker.isColliding(physical0, physical1, physical0.getPosition(), physical1.getPosition())) {
                    if(false) {
                        // elastic hit
                        Log.d(TAG,"Collision detected, performing elastic hit");
                        Vector3f u = physical0.getImpulse().add(physical1.getImpulse()).mul(2 / (physical0.getMass() + physical1.getMass()));
                        if(!physical0.isStatic()) u.sub(physical0.getVelocity(), physical0.getVelocity());
                        if(!physical1.isStatic()) u.sub(physical1.getVelocity(), physical1.getVelocity());
                    } else {
                        //pong hit
                        Log.d(TAG, "Collision detected, performing pong hit");
                        float v = physical0.getVelocity().length() + physical1.getVelocity().length();
                        Vector3f vel = physical0.getPosition().sub(physical1.getPosition(), new Vector3f()).normalize().mul(v);
                        if(!physical0.isStatic()) physical0.setVelocity(vel.mul(physical1.getMass()/(physical0.getMass() + physical1.getMass()),new Vector3f()));
                        if(!physical1.isStatic()) physical1.setVelocity(vel.mul(- physical0.getMass()/(physical0.getMass() + physical1.getMass()),new Vector3f()));
                    }
                }
            }
        }
    }
    public static void addPhysical(Physical entity) {
        physicals.add(entity);
    }
}
