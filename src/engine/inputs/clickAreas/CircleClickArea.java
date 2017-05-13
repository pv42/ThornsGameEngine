package engine.inputs.clickAreas;

import org.lwjgl.util.vector.Vector2f;

/**
 * Created by pv42 on 07.09.2016.
 */
public class CircleClickArea extends ClickArea {
    private float x,y,r;

    public CircleClickArea(float x, float y, float r) {
        this.x = x;
        this.y = y;
        this.r = r;
    }

    @Override
    public boolean isPointIn(Vector2f point) {
        float dx = x - point.x;
        float dy = y - point.y;
        return (dx * dx + dy * dy) < r*r;
    }
}
