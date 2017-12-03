package engine.graphics.nFontMeshCreator;

import engine.toolbox.IOUtil;
import engine.toolbox.Log;
import org.lwjgl.stb.STBTTFontinfo;
import org.lwjgl.stb.STBTruetype;

import java.io.IOException;
import java.nio.ByteBuffer;

import static org.lwjgl.stb.STBTruetype.stbtt_InitFont;

public class TTFont {
    private final STBTTFontinfo fontinfo;
    private int ascend;
    private int descent;
    private int lineGap;

    private TTFont(STBTTFontinfo fontinfo) {
        this.fontinfo = fontinfo;
    }
    public static TTFont loadFromFile(String file) {
        STBTTFontinfo font = STBTTFontinfo.create();
        ByteBuffer rawFont;
        try {
            rawFont = IOUtil.ioResourceToByteBuffer(file, 2048 * 1024); //todo ?
        } catch (IOException e) {
            Log.e("TTFontLoader", "Failed to open font file: " + file);
            return null;
        }
        if(!stbtt_InitFont(font, rawFont)) {
            Log.e("Failed to parse font file: " + file);
            return null;
        }
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

    public STBTTFontinfo getInfo() {
        return fontinfo;
    }

    public int getAscend() {
        return ascend;
    }

    public int getDescent() {
        return descent;
    }

    public int getLineGap() {
        return lineGap;
    }
}
