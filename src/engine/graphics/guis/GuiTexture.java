package engine.graphics.guis;

import engine.toolbox.Conversion;
import org.joml.Vector2f;
import org.joml.Vector4f;

/**
 * Created by pv42 on 20.06.16.
 */
public class GuiTexture {
    private int texture;
    private Vector2f position;
    private Vector2f scale;

    public GuiTexture(int texture, Vector2f position, Vector2f scale) {
        this.texture = texture;
        this.position = position;
        this.scale = scale;
    }

    public int getTexture() {
        return texture;
    }

    public Vector2f getPosition() {
        return position;
    }

    public Vector2f getScale() {
        return scale;
    }
    public Vector4f getArea() {
        Vector2f min = Conversion.pixelFromOpenGLSpace2D(new Vector2f(getPosition().x() - getScale().x(),getPosition().y() - getScale().y()));
        Vector2f max = Conversion.pixelFromOpenGLSpace2D(new Vector2f(getPosition().x() + getScale().x(),getPosition().y() + getScale().y()));
        return new Vector4f(min.x,min.y,max.x,max.y);
    }
}
