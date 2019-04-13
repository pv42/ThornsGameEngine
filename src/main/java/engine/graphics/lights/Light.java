package engine.graphics.lights;

import engine.toolbox.Color;

public class Light {
    private Color color;

    public Light(Color color) {
        this.color = color;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color.set(color);
    }

}
