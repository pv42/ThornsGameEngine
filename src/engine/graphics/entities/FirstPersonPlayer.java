package engine.graphics.entities;

import engine.graphics.models.TexturedModel;
import engine.inputs.InputEvent;
import engine.inputs.listeners.CursorListener;
import engine.inputs.InputHandler;
import engine.inputs.listeners.InputEventListener;
import org.lwjgl.glfw.GLFW;
import shivt.guns.Gun;
import org.joml.Vector3f;
import engine.graphics.DisplayManager;
import engine.graphics.terrains.Terrain;
import engine.toolbox.Settings;

import java.util.List;

import static engine.inputs.InputEvent.*;

/***
 * Created by pv42 on 20.06.16.
 */
public class FirstPersonPlayer extends Player{
    private static final float TURN_SPEED = Settings.MOUSE_SENSITIVITY;
    private static final float MAX_PITCH_ABS = 70;
    private float currentSpeed;
    private float currentSideSpeed;
    private int dMouseX, dMouseY;
    private float heightAngle = 0;
    private Gun gun;
    private boolean sprinting;
    public FirstPersonPlayer(List<TexturedModel> model, Vector3f position, float rx, float ry, float rz, float scale) {
        super(model,position,rx,ry,rz,scale);
        sprinting = false;
        registerEvents();
    }
    @Override
    public void move(Terrain terrain) {
        super.increaseRotation(0, dMouseX * TURN_SPEED, 0); //todo
        heightAngle += dMouseY * TURN_SPEED;
        heightAngle = Math.max(Math.min(heightAngle,MAX_PITCH_ABS),-MAX_PITCH_ABS);
        float bothFact = 1;
        if(currentSideSpeed != 0 && currentSpeed != 0) {
            bothFact = 1f / (float) Math.sqrt(2);
        }
        float distance = currentSpeed *( sprinting ? SPRINT_FACTOR : 1) * bothFact * DisplayManager.getFrameTimeSeconds();
        float dx = (float) (distance * Math.sin(Math.toRadians(super.getRy())));
        float dz = (float) (distance * Math.cos(Math.toRadians(super.getRy())));
        float distanceSide = currentSideSpeed * ( sprinting ? SPRINT_FACTOR : 1) * bothFact * DisplayManager.getFrameTimeSeconds();
        dx += (float) (distanceSide * Math.cos(Math.toRadians(super.getRy())));
        dz -= (float) (distanceSide * Math.sin(Math.toRadians(super.getRy())));
        upwardSpeed += GRAVITY * DisplayManager.getFrameTimeSeconds();
        super.increasePosition(dx, upwardSpeed * DisplayManager.getFrameTimeSeconds(), dz);
        float terrainHeight = terrain.getHeightOfTerrain(getPosition().x, getPosition().z);
        if (getPosition().y < terrainHeight) {
            upwardSpeed = 0;
            super.getPosition().y = terrainHeight;
            isInAir = false;
        }
        //gun
        if(gun != null) {
            getGun().update(this);
        }
    }
    private void registerEvents() {
        DisplayManager.setMouseGrabbed(true);
        InputHandler.setMouseBound(true);
        InputHandler.setCursorListener(new CursorListener() {
            @Override
            public void onMove(int x, int y) {
                // todo  implement
                increaseRotation(y * 0.1f, - x * 0.1f,0);
            }
        });
        InputHandler.addListener(new InputEventListener(KEY_EVENT,KEY_PRESS, GLFW.GLFW_KEY_W) {
            @Override
            public void onOccur() {
                currentSpeed = RUN_SPEED;
            }
        });
        InputHandler.addListener(new InputEventListener(KEY_EVENT,KEY_RELEASE, GLFW.GLFW_KEY_W) {
            @Override
            public void onOccur() {
                currentSpeed = 0;
            }
        });
        InputHandler.addListener(new InputEventListener(KEY_EVENT,KEY_PRESS, GLFW.GLFW_KEY_A) {
            @Override
            public void onOccur() {
                currentSideSpeed = RUN_SPEED;
            }
        });
        InputHandler.addListener(new InputEventListener(KEY_EVENT,KEY_RELEASE, GLFW.GLFW_KEY_A) {
            @Override
            public void onOccur() {
                currentSideSpeed = 0;
            }
        });
        InputHandler.addListener(new InputEventListener(KEY_EVENT,KEY_PRESS, GLFW.GLFW_KEY_S) {
            @Override
            public void onOccur() {
                currentSpeed = - RUN_SPEED;
            }
        });
        InputHandler.addListener(new InputEventListener(KEY_EVENT,KEY_RELEASE, GLFW.GLFW_KEY_S) {
            @Override
            public void onOccur() {
                currentSpeed = 0;
            }
        });
        InputHandler.addListener(new InputEventListener(KEY_EVENT,KEY_PRESS, GLFW.GLFW_KEY_D) {
            @Override
            public void onOccur() {
                currentSideSpeed = - RUN_SPEED;
            }
        });
        InputHandler.addListener(new InputEventListener(KEY_EVENT,KEY_RELEASE, GLFW.GLFW_KEY_D) {
            @Override
            public void onOccur() {
                currentSideSpeed = 0;
            }
        });
        InputHandler.addListener(new InputEventListener(KEY_EVENT,KEY_PRESS, GLFW.GLFW_KEY_LEFT_SHIFT) {
            @Override
            public void onOccur() {
                sprinting = true;
            }
        });
        InputHandler.addListener(new InputEventListener(KEY_EVENT,KEY_RELEASE, GLFW.GLFW_KEY_LEFT_SHIFT) {
            @Override
            public void onOccur() {
                sprinting = false;
            }
        });
        InputHandler.addListener(new InputEventListener(KEY_EVENT,KEY_PRESS, GLFW.GLFW_KEY_SPACE) {
            @Override
            public void onOccur() {
                jump();
            }
        });
        InputHandler.addListener(new InputEventListener(MOUSE_EVENT,KEY_PRESS, InputEvent.L_MOUSE) {
            @Override
            public void onOccur() {
                //getGun().shot(null,getThis()); // todo
            }
        });
    }
    public float getHeightAngle() {
        return heightAngle;
    }

    public Gun getGun() {
        return gun;
    }

    public void setGun(Gun gun) {
        this.gun = gun;
    }
    public Player getThis() {
        return this;
    }
}
