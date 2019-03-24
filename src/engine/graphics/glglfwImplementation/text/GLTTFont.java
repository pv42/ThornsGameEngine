package engine.graphics.glglfwImplementation.text;

import engine.graphics.text.Font;
import engine.toolbox.IOUtil;
import engine.toolbox.Log;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.stb.STBTTBakedChar;
import org.lwjgl.stb.STBTTFontinfo;
import org.lwjgl.stb.STBTruetype;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;

/**
 * a loaded TrueType font, while a .ttf font file is a vector graphic, OpenGL only accepts pixel textures, so these
 * pixel textures are created here with OpenGL and Sean's Tool Box (STB_TrueType)
 *
 * @author pv42
 */
public class GLTTFont implements Font {
    private static final String TAG = "FontTextureLoader";

    static byte CODEPOINT_OFFSET = 31;
    private static byte QUALITY_BM_SIZE_MULTIPLIER = 16;

    private STBTTBakedChar.Buffer bakedBuffer;
    private volatile STBTTFontinfo fontinfo;
    private int texture;
    private int bitmapSize;
    private int ascent;
    private int descent;
    private int lineGap;
    private Map<Integer, HMetrics> hMetricsMap;
    private int pixelSize; // fonts size in pixel
    private float scale;
    private volatile ByteBuffer fontData;

    /**
     * loads a TrueType font from a .ttf file, creates the texture with a given quality
     *
     * @param filename       font file's location
     * @param textureQuality quality of the generated texture, this should be at least 8, 32 provides a good quality
     */
    public GLTTFont(String filename, int textureQuality) {
        pixelSize = textureQuality;
        bitmapSize = QUALITY_BM_SIZE_MULTIPLIER * textureQuality;
        fontData = loadFontData(filename);
        fontinfo = loadFont(fontData);
        getVMetrics();
        bakedBuffer = bakeFont(fontData, textureQuality);
        hMetricsMap = new HashMap<>();
        scale = STBTruetype.stbtt_ScaleForPixelHeight(fontinfo, pixelSize);
    }

    private static ByteBuffer loadFontData(String filename) {
        ByteBuffer buffer;
        try {
            buffer = IOUtil.loadFileAsBuffer(filename);
        } catch (IOException ex) {
            ex.printStackTrace();
            Log.i(TAG, "failed loading font");
            buffer = BufferUtils.createByteBuffer(0);
            buffer.flip();
        }
        return buffer;
    }

    private static STBTTFontinfo loadFont(ByteBuffer buffer) {
        STBTTFontinfo info = STBTTFontinfo.create();
        if (!STBTruetype.stbtt_InitFont(info, buffer)) {
            throw new IllegalStateException("Failed to initialize font information.");
        }

        return info;
    }

    private void getVMetrics() {
        int[] pAscent = new int[1];
        int[] pDescent = new int[1];
        int[] pLineGap = new int[1];
        STBTruetype.stbtt_GetFontVMetrics(fontinfo, pAscent, pDescent, pLineGap);
        ascent = pAscent[0];
        descent = pDescent[0];
        lineGap = pLineGap[0];
    }

    private HMetrics getHMetrics(int character) {
        if (hMetricsMap.containsKey(character)) {
            return hMetricsMap.get(character);
        }
        int[] advanceWidth = new int[1];
        int[] leftSideBearing = new int[1];
        STBTruetype.stbtt_GetCodepointHMetrics(fontinfo, character, advanceWidth, leftSideBearing);
        HMetrics metrics = new HMetrics(advanceWidth[0], leftSideBearing[0]);
        hMetricsMap.put(character, metrics);
        return metrics;
    }

    private STBTTBakedChar.Buffer bakeFont(ByteBuffer fontData, int size) {
        texture = GL11.glGenTextures();
        STBTTBakedChar.Buffer bakedCharBuffer = STBTTBakedChar.malloc(96);

        ByteBuffer bitmapBB = BufferUtils.createByteBuffer(bitmapSize * bitmapSize);
        STBTruetype.stbtt_BakeFontBitmap(fontData, size, bitmapBB, bitmapSize, bitmapSize, CODEPOINT_OFFSET, bakedCharBuffer);

        GL11.glBindTexture(GL11.GL_TEXTURE_2D, texture);
        GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_ALPHA, bitmapSize, bitmapSize, 0, GL11.GL_ALPHA, GL11.GL_UNSIGNED_BYTE, bitmapBB);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);

        GL11.glClearColor(0f, 0f, 0f, 0f); // BG color
        GL11.glColor3f(200f / 255f, 200f / 255f, 200 / 255f); // Text color

        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

        return bakedCharBuffer;
    }

    /**
     * fonts additional data to the texture with all the glyphs containing the glyphs positions
     *
     * @return fonts additional data
     */
    STBTTBakedChar.Buffer getBakedBuffer() {
        return bakedBuffer;
    }

    /**
     * gets the fonts metadata
     *
     * @return fonts metadata
     */
    STBTTFontinfo getFontinfo() {
        return fontinfo;
    }

    /**
     * gets the OpenGL texture id of the bitmap with all the glyphs
     *
     * @return textures id
     */
    int getTexture() {
        return texture;
    }

    /**
     * gets the size of the generated bitmap texture containing the glyphs, the bitmap is a 2D size x size
     *
     * @return bitmaps size
     */
    public int getBitmapSize() {
        return bitmapSize;
    }

    public int getAscent() {
        return ascent;
    }

    public int getDescent() {
        return descent;
    }

    public int getLineGap() {
        return lineGap;
    }

    public int getAdvancedWidth(int character) {
        return getHMetrics(character).advancedWidth;
    }

    public int getLeftSideBearing(int character) {
        return getHMetrics(character).leftSideBearing;
    }

    float getScale() {
        return scale;
    }

    /**
     * horizontal metrics of a font containing the distance between the left side and the first character and the
     * advance width
     */
    private class HMetrics {
        int advancedWidth;
        int leftSideBearing;

        HMetrics(int advancedWidth, int leftSideBearing) {
            this.advancedWidth = advancedWidth;
            this.leftSideBearing = leftSideBearing;
        }
    }

    public int getPixelSize() {
        return pixelSize;
    }
}
