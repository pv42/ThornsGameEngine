package engine.inputs.clickAreas;

import org.joml.Vector2f;

/**
 * Created by pv42 on 07.09.2016.
 */
public interface ClickArea {
    boolean isPointIn(Vector2f point);
}
