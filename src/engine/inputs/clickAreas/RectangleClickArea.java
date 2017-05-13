package engine.inputs.clickAreas;

import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector4f;

/**
 * Created by pv42 on 07.09.2016.
 */
public class RectangleClickArea extends ClickArea {
    private float lwrX,lwrY,uprX,uprY;

    public RectangleClickArea(float lwrX, float lwrY, float uprX, float uprY) {
        this.lwrX = lwrX;
        this.lwrY = lwrY;
        this.uprX = uprX;
        this.uprY = uprY;
    }
    public RectangleClickArea(Vector4f pos) {
        lwrX = pos.x;
        lwrY = pos.y;
        uprX = pos.z;
        uprY = pos.w;
    }

    @Override
    public boolean isPointIn(Vector2f point) {
        return point.x > lwrX && point.y > lwrY && point.x < uprX && point.y < uprY;
    }
}
