package engine.inputs.clickAreas;

import org.lwjgl.util.vector.Vector2f;

/**
 * Created by pv42 on 07.09.2016.
 */
public abstract class ClickArea {
    public abstract boolean isPointIn(Vector2f point);
}
