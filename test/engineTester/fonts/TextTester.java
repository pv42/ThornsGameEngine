package engineTester.fonts;

import engine.graphics.nFontMeshCreator.FontTextureRenderer;
import engine.graphics.nFontMeshCreator.TTFont;
import engine.graphics.textures.TextureData;
import engine.toolbox.IOUtil;
import engine.toolbox.Log;
import org.lwjgl.BufferUtils;
import org.lwjgl.stb.*;
import org.lwjgl.system.MemoryUtil;

import java.io.*;
import java.nio.ByteBuffer;

public class TextTester {
    private static final String TAG = "FontTester";

    public static void main(String args[]) throws Throwable {
        test1();
        test2();
    }
    private static void test1() throws IOException{
        String fontPath = "C:\\Windows\\Fonts\\Arial.ttf";
        File fontFile = new File(fontPath);
        Log.d(TAG, "file ex.:" + fontFile.exists());
        ByteBuffer fontBytes = IOUtil.ioResourceToByteBuffer(fontPath, (int) fontFile.length());
        STBTTFontinfo fontinfo;
        fontinfo = STBTTFontinfo.create();
        if(!STBTruetype.stbtt_InitFont(fontinfo, fontBytes)) {
            Log.e("Nope");
            //fontinfo.free();
            throw new IllegalStateException("failed to init! (adr= " + fontinfo.address() + ", size=" + fontinfo.sizeof() + ")");
        }
        Log.d(TAG, "font loaded");
        int bitmapHeight = 512;
        int bitmapWidth = 4608;
        int lineHeight = 256;
        float scale = STBTruetype.stbtt_ScaleForPixelHeight(fontinfo, lineHeight);
        int[] asc = new int[1];
        int[] dsc = new int[1];
        int[] lg = new int[1];
        STBTruetype.stbtt_GetFontVMetrics(fontinfo,asc,dsc,lg);
        ByteBuffer bitmap = BufferUtils.createByteBuffer(bitmapHeight * bitmapWidth);
        asc[0] *= scale;
        dsc[0] *= scale;
        String text = "Akgj^|Î#'?§â Dsu 45w e f6e35 sr t#a -wr!cv.";
        int x = 0;
        for (int i = 0; i < text.length(); i++) {
            char c1,c0 = text.toCharArray()[i];
            if(i == text.length() - 1) {
                c1 = ' ';
            } else {
                c1 = text.toCharArray()[i+1];
            }
            int[] charX0 = new int[1];
            int[] charX1 = new int[1];
            int[] charY0 = new int[1];
            int[] charY1 = new int[1];
            STBTruetype.stbtt_GetCodepointBitmapBox(fontinfo, c0, scale, scale, charX0, charY0, charX1, charY1);
            int y = asc[0] + charY0[0];
            int byteOffset = x + (y * bitmapWidth);
            Log.d("byteO=" + byteOffset + " y=" + y + " x=" + x);
            Log.d(TAG, "charX0=" + charX0[0] + " charY0=" + charX0[0] + " charX1=" + charX1[0] + " charY1=" + charY1[0] + " x=" +x);
            STBTruetype.nstbtt_MakeCodepointBitmap(fontinfo.address(), MemoryUtil.memAddress(bitmap) + byteOffset, charX1[0]- charX0[0],charY1[0]-charY0[0],bitmapWidth,scale,scale,c0);
            int[] ax = new int[1];
            int[] idc = new int[1];
            STBTruetype.stbtt_GetCodepointHMetrics(fontinfo, c0, ax, idc);
            Log.d(TAG, "ax=" + ax[0]);
            x+= ax[0] * scale;
            int kern = STBTruetype.stbtt_GetCodepointKernAdvance(fontinfo, c0, c1);
            Log.d("kern "+ kern);

            x += kern * scale;
        }
        STBImageWrite.stbi_write_png("test1.png",bitmapWidth,bitmapHeight,1,bitmap,0);
        Log.i("finished, freeing mem");
        //fontinfo.free(); //WHY DOES THIS PRODUCE MEM AC VIOLATIONS ???
    }
    private static void test2() throws IOException{
        String fontPath = "C:\\Windows\\Fonts\\arial.ttf";
        TTFont font = TTFont.loadFromFile(fontPath);
        Log.i("rendering ... ");
        TextureData fontTexture = FontTextureRenderer.renderTextToBitmap(font,"Akgj^|Î#'?§â Dsu 45w e f6e35 sr t#a -wr!cv.", 256);
        Log.i("writing ...");
        STBImageWrite.stbi_write_png("test2.png", fontTexture.getWidth(), fontTexture.getHeight(),1, fontTexture.getBuffer(),0);
        //STBImageWrite.stbi_write_bmp("test2.bmp", fontTexture.getWidth(), fontTexture.getHeight(),1, fontTexture.getBitmap());
        Log.i("done.");
    }
}