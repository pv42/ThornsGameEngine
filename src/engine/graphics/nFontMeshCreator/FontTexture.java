package engine.graphics.nFontMeshCreator;

import java.nio.ByteBuffer;

public class FontTexture {
    private ByteBuffer bitmap;
    private int width;
    private int height;

    public FontTexture(ByteBuffer bitmap, int width, int height) {
        this.bitmap = bitmap;
        this.width = width;
        this.height = height;
    }

    public ByteBuffer getBitmap() {
        return bitmap;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }
}
