package engine.graphics.lights;

import engine.toolbox.Color;
import org.joml.Vector3f;

public class SpotLight extends Light{
    public SpotLight(Vector3f position, Color color) {
        super(position, color);
    }

    public SpotLight(Vector3f position, Color color, Vector3f attenuation) {
        super(position, color, attenuation);
    }
    //todo
}
