package engine.graphics.glglfwImplementation.text;

import engine.graphics.text.GuiText;
import engine.graphics.text.Font;
import engine.toolbox.Color;
import org.joml.Vector2f;


/**
 * A renderable text e.g a string to be rendered in a font, color and size at a starting specific position
 *
 * @author pv42
 */
public class GLGuiText implements GuiText {
    private GLTTFont font;
    private String string;
    private float size;
    private Color color;
    private Vector2f position;

    /**
     * creates a text, requires no loading to be done since information are stored in the font
     *
     * @param font     text true type font of the text
     * @param string   guiTexts text
     * @param size     texts size
     * @param color    texts font color
     * @param position texts base position
     */
    public GLGuiText(GLTTFont font, String string, float size, Color color, Vector2f position) {
        this.font = font;
        this.string = string;
        this.size = size;
        this.color = color;
        this.position = position;
    }

    /**
     * gets the text (true type) font
     *
     * @return texts ttf
     */
    public GLTTFont getFont() {
        return font;
    }

    /**
     * gets the guiTexts actual text as string
     *
     * @return texts string
     */
    public String getString() {
        return string;
    }

    /**
     * gets the guiTexts font size
     *
     * @return texts font size
     */
    public float getSize() {
        return size;
    }

    /**
     * gets the guiTexts color
     *
     * @return texts color
     */
    public Color getColor() {
        return color;
    }

    /**
     * gets the texts first character's position
     *
     * @return first chars position
     */
    public Vector2f getPosition() {
        return position;
    }

    /**
     * sets text true type font
     *
     * @param font GLTTFont to use
     */
    public void setFont(Font font) {
        if(!(font instanceof GLTTFont)) throw new UnsupportedOperationException("expected GLTTTFFont");
        this.font = (GLTTFont) font;
    }

    /**
     * sets the guiText string
     *
     * @param string text string data
     */
    public void setString(String string) {
        this.string = string;
    }

    /**
     * sets texts font size
     *
     * @param size font size
     */
    public void setSize(float size) {
        this.size = size;
    }

    /**
     * sets guiTexts text color
     *
     * @param color color to set
     */
    public void setColor(Color color) {
        this.color = color;
    }

    /**
     * sets text starting position
     *
     * @param position text starting position
     */
    public void setPosition(Vector2f position) {
        this.position = position;
    }
}
