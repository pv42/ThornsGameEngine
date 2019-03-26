package engine.graphics.text;

import engine.toolbox.Color;
import org.joml.Vector2f;



/**
 * A renderable text e.g a string to be rendered in a font, color and size at a starting specific position
 *
 * @author pv42
 */
public interface Text {


    /**
     * gets the text (true type) font
     *
     * @return texts ttf
     */
    Font getFont();

    /**
     * sets text true type font
     *
     * @param font GLTTFont to use
     */
    void setFont(Font font);

    /**
     * gets the guiTexts actual text as string
     *
     * @return texts string
     */
    String getString();

    /**
     * sets the guiText string
     *
     * @param string text string data
     */
    void setString(String string);

    /**
     * gets the guiTexts font size
     *
     * @return texts font size
     */
    float getSize();

    /**
     * sets texts font size
     *
     * @param size font size
     */
    void setSize(float size);

    /**
     * gets the guiTexts color
     *
     * @return texts color
     */
    Color getColor();

    /**
     * sets guiTexts text color
     *
     * @param color color to set
     */
    void setColor(Color color);

    /**
     * gets the texts first character's position
     *
     * @return first chars position
     */
    Vector2f getPosition();

    /**
     * sets text starting position
     *
     * @param position text starting position
     */
    void setPosition(Vector2f position);
}
