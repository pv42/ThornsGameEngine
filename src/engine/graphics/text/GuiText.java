package engine.graphics.text;

import engine.toolbox.Color;
import org.joml.Vector2f;


/**
 * A renderable text e.g. a string to be rendered in a font, color and size at a starting specific position
 *
 * @author pv42
 */
public interface GuiText {


    /**
     * gets the text font
     *
     * @return texts fonts
     */
    Font getFont();

    /**
     * sets text font
     *
     * @param font font to use for the text
     */
    void setFont(Font font);

    /**
     * gets the guiTexts actual text as string
     *
     * @return texts string
     */
    String getString();

    /**
     * sets the guiTexts text string
     *
     * @param string text string data
     */
    void setString(String string);

    /**
     * gets the guiTexts font size; a size of 1 means pixel in the original font file is 1 in openGL space, so this
     * should be around 1e-5 to result in usable text
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
     * gets the guiTexts (foreground) color, the background will be transparent
     *
     * @return texts color
     */
    Color getColor();

    /**
     * sets guiTexts text (foreground) color, the background will be transparent
     *
     * @param color color to set
     */
    void setColor(Color color);

    /**
     * gets the texts first character's position in openGL space
     *
     * @return first chars position
     */
    Vector2f getPosition();

    /**
     * sets text starting position in openGL space, so 0,0 is the center of the window and -1,1 is top left
     *
     * @param position text starting position
     */
    void setPosition(Vector2f position);
}
