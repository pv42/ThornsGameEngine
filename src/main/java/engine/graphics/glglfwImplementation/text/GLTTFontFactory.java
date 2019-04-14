package engine.graphics.glglfwImplementation.text;


import engine.graphics.text.FontFactory;
import engine.toolbox.Log;
import org.lwjgl.system.Platform;

import java.io.File;
import java.io.FileNotFoundException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * a FontFactory for GLTTFFonts
 *
 * @author pv42
 */
public class GLTTFontFactory implements FontFactory {
    private static final String TAG = "GLTTFontFactory";
    private final String[] FONT_PATHS;
    private Map<String, List<GLTTFont>> fonts;

    public GLTTFontFactory() {
        fonts = new HashMap<>();
        switch (Platform.get()) {
            case WINDOWS:
                FONT_PATHS = new String[]{System.getenv("SystemRoot") + "\\" + "fonts" + "\\"};
                break;
            case MACOSX:
                Log.w(TAG, "macOS is system font loading is not supported");
                FONT_PATHS = new String[]{};
                break;
            case LINUX:
                FONT_PATHS = new String[]{System.getProperty("user.home") + "/.local/share/fonts/","/usr/share/fonts/"};
                break;
            default:
                Log.w(TAG, "operation system " + Platform.get() + " is not supported for system font loading");
                FONT_PATHS = new String[]{};
        }
    }

    public GLTTFont getSystemFont(String name, int minPixelSize) {
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
        return loadSystemFont(name, minPixelSize);
    }

    private GLTTFont loadSystemFont(String name, int pixelSize) {
        String path;
        try {
            path = getSystemFontPath(name);
        } catch (FileNotFoundException e) {
            Log.e(TAG, "could not load font:" + name);
            if(fonts.size() > 0) return fonts.get(fonts.keySet().iterator().next()).get(0); // fallback to some random font
            throw new IllegalStateException("could not find a valid font");
        }
        GLTTFont font = new GLTTFont(path, pixelSize);
        if (!fonts.containsKey(name)) {
            fonts.put(name, new ArrayList<>());
        }
        fonts.get(name).add(font);
        return font;


    }

    private String getSystemFontPath(String name) throws FileNotFoundException {
        for(String base: FONT_PATHS) {
            String path = base + name +".ttf";
            if(Files.exists(Path.of(path))) {
                return path;
            }
        }
        throw new FileNotFoundException("could not locate font " + name);
    }

    public void clear() {
        fonts = null;
    }
}
