package engine.toolbox.assimpLoader;

import org.lwjgl.assimp.AITexture;

public class AssimpTexture {
    private final int width;
    private final int height;
    String filename;
    private AssimpTexture(int width, int height, String filename) {
        this.width = width;
        this.height = height;
        this.filename = filename;
        System.out.println(this);
    }
    static AssimpTexture load(AITexture aiTexture) {
        //int w = aiTexture.mWidth();
        //int h = aiTexture.mHeight();
        //String fn = aiTexture.mFilename().dataString();
        //AssimpTexture texture = new AssimpTexture(w,h,fn);
        return null; // texture loading disabled since there is a assimp-lwjgl error
    }

    @Override
    public String toString() {
        return "AssimpTexture{" +
                "width=" + width +
                ", height=" + height +
                ", filename='" + filename + '\'' +
                '}';
    }
}
