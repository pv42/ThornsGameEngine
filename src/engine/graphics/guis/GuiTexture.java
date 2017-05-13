package engine.graphics.guis;

import engine.toolbox.Convertation;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector4f;

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
        Vector2f min = Convertation.pixelFromOpenGLSpace2D(new Vector2f(getPosition().getX() - getScale().getX(),getPosition().getY() - getScale().getY()));
        Vector2f max = Convertation.pixelFromOpenGLSpace2D(new Vector2f(getPosition().getX() + getScale().getX(),getPosition().getY() + getScale().getY()));
        return new Vector4f(min.x,min.y,max.x,max.y);
    }
}
