package engine.graphics.nFontMeshCreator;

import engine.toolbox.IOUtil;
import engine.toolbox.Log;
import org.lwjgl.stb.STBTTFontinfo;
import org.lwjgl.stb.STBTruetype;

import java.io.IOException;
import java.nio.ByteBuffer;

import static org.lwjgl.stb.STBTruetype.stbtt_InitFont;

/**
 * A loaded TrueTypeFont (*.ttf) witch can be used to render text.
 * @author pv42
 */
public class TTFont {
    private static final String TAG = "TTFontLoader";
    private final STBTTFontinfo fontinfo;
    private int ascend;
    private int descent;
    private int lineGap;

    private TTFont(STBTTFontinfo fontinfo) {
        this.fontinfo = fontinfo;
    }

    /**
     * loads a ttf file
     * @param file file to load
     * @return loaded TrueTypeFont
     */
    public static TTFont loadFromFile(String file) {
        STBTTFontinfo font = STBTTFontinfo.create();
        ByteBuffer rawFont;
        try {
            rawFont = IOUtil.ioResourceToByteBuffer(file, 2048 * 1024); //todo ?
        } catch (IOException e) {
            Log.e(TAG, "Failed to open font file: " + file);
            return null;
        }
        if(!stbtt_InitFont(font, rawFont)) {
            Log.e(TAG,"Failed to parse font file: " + file);
            return null;
        }
        Log.d(TAG, "Font loaded: " + file);
        TTFont ttFont = new TTFont(font);
        int[] asc = new int[1];
        int[] dsc = new int[1];
        int[] lineGap = new int[1];
        STBTruetype.stbtt_GetFontVMetrics(font,asc,dsc,lineGap);
        ttFont.ascend = asc[0];
        ttFont.descent = dsc[0];
        ttFont.lineGap = lineGap[0];
        return ttFont;
    }

    /**
     * Returns the fonts data (in STBTrueType format) witch is includes the character data
     * @return fonts info
     */
    public STBTTFontinfo getInfo() {
        return fontinfo;
    }

    /**
     * returns the fonts ascend (max vertical size from the baseline up)
     * @return fonts ascend
     */
    public int getAscend() {
        return ascend;
    }

    /**
     * returns the fonts descend (max vertical size from the baseline down)
     * @return fonts descent
     */
    public int getDescent() {
        return descent;
    }

    /**
     * returns the fonts gap between lines
     * @return line gap
     */
    public int getLineGap() {
        return lineGap;
    }
}
