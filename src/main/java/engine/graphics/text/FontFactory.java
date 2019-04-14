package engine.graphics.text;

import engine.graphics.glglfwImplementation.text.GLTTFont;

/**
 * A FontFactory loads fonts
 *
 * @author pv42
 */
public interface FontFactory {
    /**
     * gets a system font, if already loaded in minimum quality or higher return it, otherwise loads it
     * @param name the fonts name
     * @param minPixelSize minimum pixelSize/quality on the font
     * @return the loaded font
     */
    GLTTFont getSystemFont(String name, int minPixelSize);

    /**
     * clears the FontFactory, unloads all fonts
     */
    void clear();
}
