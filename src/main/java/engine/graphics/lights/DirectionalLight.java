package engine.graphics.lights;

import engine.toolbox.Color;
import org.joml.Vector3f;

public class DirectionalLight extends Light {
    private Vector3f direction;

    public DirectionalLight(Color color, Vector3f direction) {
        super(color);
        this.direction = direction;
    }

    public Vector3f getDirection() {
        return direction;
    }
}
