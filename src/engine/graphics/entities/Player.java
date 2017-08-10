package engine.graphics.entities;

import engine.graphics.models.TexturedModel;
import org.joml.Vector3f;
import engine.graphics.terrains.Terrain;
import engine.toolbox.Settings;

import java.util.List;

/***
 * Created by pv42 on 20.06.16.
 */
public abstract class Player extends Entity{
    static final float RUN_SPEED = 30;
    static final float SPRINT_FACTOR = 1.5f;
    private static final float JUMP_POWER = 30;
    private static final float EYE_HEIGHT = 4.6f;
    float upwardSpeed;
    boolean isInAir;
    public Player(List<TexturedModel> model, Vector3f position, float rx, float ry, float rz, float scale) {
        super(model,position,rx,ry,rz,scale);
    }

    public abstract void move(Terrain terrain);
    void jump() {
        if(!isInAir) {
            this.upwardSpeed = JUMP_POWER;
            isInAir = true;
        }
    }

    public Vector3f getEyePosition() {
        Vector3f eyePosition = new Vector3f(getPosition());
        eyePosition.y += EYE_HEIGHT;
        return eyePosition;
    }
}
