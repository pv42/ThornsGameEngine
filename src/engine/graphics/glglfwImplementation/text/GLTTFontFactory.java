package engine.graphics.glglfwImplementation.text;


import org.lwjgl.system.Platform;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class GLTTFontFactory {
    private static String SYSTEM_FONT_PATH;
    private static Map<String, List<GLTTFont>> fonts = new HashMap<>();
    public static GLTTFont loadSystemFont(String name, int minPixelSize) {
        if (fonts.containsKey(name)) {
            GLTTFont bestFont = null;
            int pixelSize = Integer.MAX_VALUE;
            for (GLTTFont font : fonts.get(name)) {
                if (font.getPixelSize() >= minPixelSize && font.getPixelSize() < pixelSize) {
                    bestFont = font;
                    pixelSize = font.getPixelSize();
                }
            }
            if (bestFont != null) return bestFont;
        }
        return loadFont(name, minPixelSize);
    }

    private static GLTTFont loadFont(String name, int pixelSize) {
        if(SYSTEM_FONT_PATH == null) getSystemFontPath();
        String path = SYSTEM_FONT_PATH + name + ".ttf";
        GLTTFont font = new GLTTFont(path, pixelSize);
        if (!fonts.containsKey(name)) {
            fonts.put(name, new ArrayList<>());
        }
        fonts.get(name).add(font);
        return font;
    }

    private static void getSystemFontPath() {
        switch (Platform.get()) {
            case WINDOWS:
                SYSTEM_FONT_PATH = System.getenv("SystemRoot") + "\\" + "fonts" + "\\";
                break;
            case MACOSX:
                SYSTEM_FONT_PATH = "/System/Library/Fonts/";
                break;
            case LINUX:
                SYSTEM_FONT_PATH = "/usr/share/fonts/";
                break;
            default:
                throw new UnsupportedOperationException("operation system " + Platform.get() + " is not supported");
        }
    }

    public static void clear() {
        fonts = new HashMap<>();
    }
}
