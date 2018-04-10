package engine.graphics.nFontMeshCreator;

import engine.graphics.textures.TextureData;
import engine.toolbox.Log;
import org.lwjgl.BufferUtils;
import org.lwjgl.system.MemoryUtil;

import static org.lwjgl.stb.STBTruetype.*;

import java.nio.ByteBuffer;

/**
 * Used to render Font textures
 * @author pv42
 */
public class FontTextureRenderer {
    private static final String TAG = "FontTextureRenderer";

    /**
     * Renders a text in a specific font and size into a 1-lined, 8bit-Bitmap
     * @param font font to use
     * @param text text to render
     * @param lineHeight line height of the rendered Text
     * @return rendered Bitmap with height and width
     */
    public static TextureData renderTextToBitmap(TTFont font, String text, int lineHeight) {
        int bitmapWidth = 0;
        int bitmapHeight;
        float scale = stbtt_ScaleForPixelHeight(font.getInfo(), lineHeight);
        int dim[][] = new int[text.length()][5];
        for(int i = 0; i < text.length(); i++) {
            int x0[] = new int[1];
            int x1[] = new int[1];
            int y0[] = new int[1];
            int y1[] = new int[1];
            stbtt_GetCodepointBitmapBox(font.getInfo(),text.charAt(i),scale,scale,x0,y0,x1,y1);
            dim[i][0] = x1[0] - x0[0];
            dim[i][1] = y1[0] - y0[0];
            dim[i][2] = y0[0];
            int advanceW[] = new int [1];
            int idc[] = new int[1]; // i don't care about this
            stbtt_GetCodepointHMetrics(font.getInfo(), text.charAt(i), advanceW, idc);
            dim[i][3] = advanceW[0];
            int kern = i==0 ? 0 : stbtt_GetCodepointKernAdvance(font.getInfo(), text.charAt(i-1),text.charAt(i));
            dim[i][4] = kern;
            bitmapWidth += (kern + advanceW[0]) * scale;
        }
        bitmapHeight = (int) ((font.getAscend() - font.getDescent()) * scale);
        ByteBuffer bitmap = BufferUtils.createByteBuffer(bitmapWidth * bitmapHeight);
        long bitmapAddress = MemoryUtil.memAddress(bitmap);
        int x = 0;
        for(int i = 0; i < text.length(); i++) {
            int y = (int)(font.getAscend() * scale) + dim[i][2];
            int byteOffset = x + (y * bitmapWidth);
            x += dim[i][4] * scale;
            nstbtt_MakeCodepointBitmap(font.getInfo().address(), bitmapAddress + byteOffset,
                    dim[i][0], dim[i][1], bitmapWidth, scale, scale, text.charAt(i));
            x += dim[i][3] * scale;
        }
        return new TextureData(bitmap, bitmapWidth, bitmapHeight);
    }
}
