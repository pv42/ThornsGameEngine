package engine.graphics.glglfwImplementation.entities;

import engine.graphics.glglfwImplementation.display.GLFWWindow;
import engine.graphics.glglfwImplementation.models.GLMaterializedModel;
import engine.inputs.InputEvent;
import engine.inputs.InputHandler;
import engine.inputs.listeners.InputEventListener;
import org.joml.Vector2f;
import org.lwjgl.glfw.GLFW;
import org.joml.Vector3f;
import engine.graphics.terrains.Terrain;
import engine.toolbox.Settings;

import java.util.List;

import static engine.inputs.InputEvent.*;
import static engine.toolbox.Settings.GRAVITY;

/***
 * Created by pv42 on 20.06.16.
 */
public class FirstPersonPlayer extends Player{
    private static final float TURN_SPEED = Settings.MOUSE_SENSITIVITY;
    private static final float MAX_PITCH_ABS = 70;
    private static final String TAG = "FPPlayer";
    private float currentSpeed;
    private float currentSideSpeed;
    private float heightAngle = 0;
    private boolean sprinting;
    public FirstPersonPlayer(List<GLMaterializedModel> model, Vector3f position, GLFWWindow window) {
        super(model,position);
        super.increaseRotation(new Vector3f(-90,0,0));
        sprinting = false;
        registerEvents(window);
    }
    @Override
    public void move(Terrain terrain, float timeDelta) {
        heightAngle = Math.max(Math.min(heightAngle,MAX_PITCH_ABS),-MAX_PITCH_ABS);
        float bothFact = 1;
        if(currentSideSpeed != 0 && currentSpeed != 0) {
            bothFact = 1f / (float) Math.sqrt(2);
        }
        float distance = currentSpeed *( sprinting ? SPRINT_FACTOR : 1) * bothFact * timeDelta;
        float dx = (float) (distance * Math.sin(Math.toRadians(super.getRz())));
        float dz = (float) (distance * Math.cos(Math.toRadians(super.getRz())));
        float distanceSide = currentSideSpeed * ( sprinting ? SPRINT_FACTOR : 1) * bothFact * timeDelta;
        dx += (float) (distanceSide * Math.cos(Math.toRadians(super.getRz())));
        dz -= (float) (distanceSide * Math.sin(Math.toRadians(super.getRz())));
        upwardSpeed -= GRAVITY * timeDelta; // todo move to physics
        super.increasePosition(dx, upwardSpeed * timeDelta, dz);
        float terrainHeight = terrain.getHeightOfTerrain(getPosition().x, getPosition().z);
        if (getPosition().y < terrainHeight) {
            upwardSpeed = 0;
            super.getPosition().y = terrainHeight;
            isInAir = false;
        }
    }
    private void registerEvents(GLFWWindow window) {
        window.setMouseGrabbed(true, window);
        InputHandler.setMouseBound(true);
        InputHandler.setCursorListener((x, y) -> {
            increaseRotation(0, 0,- x * 0.1f);
            heightAngle += y;
        });
        InputHandler.addListener(new InputEventListener(KEY_EVENT,KEY_PRESS, GLFW.GLFW_KEY_W) {
            @Override
            public void onOccur(Vector2f v2f) {
                currentSpeed = RUN_SPEED;
            }
        });
        InputHandler.addListener(new InputEventListener(KEY_EVENT,KEY_RELEASE, GLFW.GLFW_KEY_W) {
            @Override
            public void onOccur(Vector2f v2f) {
                currentSpeed = 0;
            }
        });
        InputHandler.addListener(new InputEventListener(KEY_EVENT,KEY_PRESS, GLFW.GLFW_KEY_A) {
            @Override
            public void onOccur(Vector2f v2f) {
                currentSideSpeed = RUN_SPEED;
            }
        });
        InputHandler.addListener(new InputEventListener(KEY_EVENT,KEY_RELEASE, GLFW.GLFW_KEY_A) {
            @Override
            public void onOccur(Vector2f v2f) {
                currentSideSpeed = 0;
            }
        });
        InputHandler.addListener(new InputEventListener(KEY_EVENT,KEY_PRESS, GLFW.GLFW_KEY_S) {
            @Override
            public void onOccur(Vector2f v2f) {
                currentSpeed = - RUN_SPEED;
            }
        });
        InputHandler.addListener(new InputEventListener(KEY_EVENT,KEY_RELEASE, GLFW.GLFW_KEY_S) {
            @Override
            public void onOccur(Vector2f v2f) {
                currentSpeed = 0;
            }
        });
        InputHandler.addListener(new InputEventListener(KEY_EVENT,KEY_PRESS, GLFW.GLFW_KEY_D) {
            @Override
            public void onOccur(Vector2f v2f) {
                currentSideSpeed = - RUN_SPEED;
            }
        });
        InputHandler.addListener(new InputEventListener(KEY_EVENT,KEY_RELEASE, GLFW.GLFW_KEY_D) {
            @Override
            public void onOccur(Vector2f v2f) {
                currentSideSpeed = 0;
            }
        });
        InputHandler.addListener(new InputEventListener(KEY_EVENT,KEY_PRESS, GLFW.GLFW_KEY_LEFT_SHIFT) {
            @Override
            public void onOccur(Vector2f v2f) {
                sprinting = true;
            }
        });
        InputHandler.addListener(new InputEventListener(KEY_EVENT,KEY_RELEASE, GLFW.GLFW_KEY_LEFT_SHIFT) {
            @Override
            public void onOccur(Vector2f v2f) {
                sprinting = false;
            }
        });
        InputHandler.addListener(new InputEventListener(KEY_EVENT,KEY_PRESS, GLFW.GLFW_KEY_SPACE) {
            @Override
            public void onOccur(Vector2f v2f) {
                jump();
            }
        });
        InputHandler.addListener(new InputEventListener(MOUSE_EVENT,KEY_PRESS, InputEvent.L_MOUSE) {
            @Override
            public void onOccur(Vector2f v2f) {
                //getGun().shot(null,getThis()); // todo removed guns
            }
        });
    }
    public float getHeightAngle() {
        return heightAngle;
    }
}
