package engineTester;

import engine.toolbox.IOUtil;
import engine.toolbox.Log;
import org.lwjgl.BufferUtils;
import org.lwjgl.stb.*;

import java.io.*;
import java.nio.ByteBuffer;

public class TextTester {
    private static final String TAG = "FontTester";

    public static void main(String args[]) throws IOException {
        String fontPath = "C:\\Windows\\Fonts\\ariblk.ttf";
        File fontFile = new File(fontPath);
        ByteBuffer fontBytes = IOUtil.ioResourceToByteBuffer(fontPath, (int) fontFile.length() * 2);
        STBTTFontinfo fontinfo;
        fontinfo = STBTTFontinfo.create();
        if(!STBTruetype.stbtt_InitFont(fontinfo, fontBytes)) {
            Log.e("Nope");
            fontinfo.free();
            throw new IllegalStateException("failed to init! (adr= " + fontinfo.address() + ", size=" + fontinfo.sizeof() + ")");
        }
        Log.i(TAG, "font loaded");
        int[] asc = new int[1];
        int[] dsc = new int[1];
        int[] lg = new int[1];
        STBTruetype.stbtt_GetFontVMetrics(fontinfo,asc,dsc,lg);
        Log.i(TAG, asc[0] + " " + dsc[0] + " " + lg[0]);
        int bitmapHeight = 512;
        int bitmapWidth = 512;
        int lineHeight = 32;
        ByteBuffer bitmap = BufferUtils.createByteBuffer(bitmapHeight * bitmapWidth);
        Log.i("getting height");
        float scale = STBTruetype.stbtt_ScaleForPixelHeight(fontinfo, lineHeight);
        String text = "Hello";
        int x = 0;
        Log.i("starting ...");
        for (int i = 0; i < text.length(); i++) {
            char c1,c0 = text.toCharArray()[i];
            if(i == text.length() - 1) {
                c1 = ' ';
            } else {
                c1 = text.toCharArray()[i+1];
            }
            Log.i(TAG,"c0/1:" + c0 + "/" + c1);
            int[] charX0 = new int[1];
            int[] charX1 = new int[1];
            int[] charY0 = new int[1];
            int[] charY1 = new int[1];
            STBTruetype.stbtt_GetCodepointBitmapBox(fontinfo, c0, scale, scale, charX0, charY0, charX1, charY1);
            int y = asc[0] + charX0[0];
            int byteOffset = x + (y * bitmapWidth);
            Log.i(TAG, "charX0=" + charX0[0] + " charY0=" + charX0[0] + " charX1=" + charX1[0] + " charY1=" + charY1[0]);
            STBTruetype.stbtt_MakeCodepointBitmap(fontinfo,bitmap,bitmapWidth,bitmapHeight,byteOffset,scale,scale,c0);
            int[] ax = new int[1];
            int[] idc = new int[1];
            STBTruetype.stbtt_GetCodepointHMetrics(fontinfo, c0, ax, idc);
            Log.i(TAG, "ax=" + ax[0]);
            x+= ax[0] * scale;
            int kern;
            kern = STBTruetype.stbtt_GetCodepointKernAdvance(fontinfo, c0, c1);
            x += kern * scale;
        }
        fontinfo.free();
        //STBImageWrite.stbi_write_bmp("out.bmp",bitmapWidth,bitmapHeight,1,bitmap);
        //STBImageWrite.stbi_write_png("out.png",bitmapWidth,bitmapHeight,1,bitmap,0);

    }
}