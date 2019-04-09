package engine.graphics.lights;

import engine.toolbox.Color;
import org.joml.Vector3f;

/**
 * Created by pv42 on 17.06.16.
 *
 * @author pv42
 */
public class Light {
    private Vector3f position;
    private Color color;
    private Vector3f attenuation;

    /**
     * creates a light source with a given light color at a given position, which does not loses brightness with
     * distance
     *
     * @param position lights position as a xyz vector in world space
     * @param color    lights color as RGB vector in [0..1]x[0..1]x[0..1]
     */
    public Light(Vector3f position, Color color) {
        this.position = position;
        this.color = color;
        this.attenuation = new Vector3f(1, 0, 0);
    }

    /**
     * creates a light source with a given light color at a given position and a given attenuation
     *
     * @param position    lights position as a xyz vector in world space
     * @param color       lights color as RGB vector in [0..1]x[0..1]x[0..1]
     * @param attenuation the attenuation indicates how the light decays with range, the x component is the base
     *                    brightness the y component is linear decay per distance and the z component is quadratic decay
     *                    with range, the result must be smaller then 1 in all cases to prevent render artifacts
     */
    public Light(Vector3f position, Color color, Vector3f attenuation) {
        this.position = position;
        this.color = color;
        this.attenuation = attenuation;
    }

    public Vector3f getAttenuation() {
        return attenuation;
    }

    public Vector3f getPosition() {
        return position;
    }

    public void setPosition(Vector3f position) {
        this.position.set(position);
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color.set(color);
    }

    public void move(Vector3f deltaPosition) {
        position.add(deltaPosition);
    }
}
