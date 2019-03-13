package engine.graphics.glglfwImplementation.entities;

import engine.graphics.models.TexturedModel;
import org.joml.Vector3f;
import engine.graphics.terrains.Terrain;

import java.util.List;

import static engine.toolbox.Settings.GRAVITY;

/***
 * Created by pv42 on 20.06.16.
 */
public class ThirdPersonPlayer extends Player{
    private static final float TURN_SPEED = 160;
    private float currentSpeed;
    private float currentTurnSpeed;
    public ThirdPersonPlayer(List<TexturedModel> model, Vector3f position) {
        super(model,position);
    }
    @Override
    public void move(Terrain terrain, float timeDelta) {
        checkInputs();
        super.increaseRotation(0, currentTurnSpeed * timeDelta, 0);
        float distance = currentSpeed * timeDelta;
        float dx = (float) (distance * Math.sin(Math.toRadians(super.getRy())));
        float dz = (float) (distance * Math.cos(Math.toRadians(super.getRy())));
        upwardSpeed -= GRAVITY * timeDelta; // todo move to physics

        super.increasePosition(dx, upwardSpeed * timeDelta, dz);
        float terrainHeigt = terrain.getHeightOfTerrain(getPosition().x, getPosition().z);
        if (getPosition().y < terrainHeigt) { //todo !!!!!!
            upwardSpeed = 0;
            super.getPosition().y = terrainHeigt;
            isInAir = false;
        }
    }
    private void checkInputs() {
        float sprintFactor = 1;
        /*
        if(Keyboard.isKeyDown(Keyboard.KEY_LSHIFT)) {
            sprintFactor = SPRINT_FACTOR;
        }
        if(Keyboard.isKeyDown(Keyboard.KEY_W)) {
            this.currentSpeed = RUN_SPEED * sprintFactor;
        } else if(Keyboard.isKeyDown(Keyboard.KEY_S)) {
            this.currentSpeed = - RUN_SPEED * sprintFactor;
        } else {
            this.currentSpeed = 0;
        }
        if(Keyboard.isKeyDown(Keyboard.KEY_D)) {
            this.currentTurnSpeed =  - TURN_SPEED;
        } else if(Keyboard.isKeyDown(Keyboard.KEY_A)) {
            this.currentTurnSpeed = TURN_SPEED;
        } else  {
            currentTurnSpeed = 0;
        }
        if(Keyboard.isKeyDown(Keyboard.KEY_SPACE)) {
            super.jump();
        }
        */
        //todo key
    }
}
