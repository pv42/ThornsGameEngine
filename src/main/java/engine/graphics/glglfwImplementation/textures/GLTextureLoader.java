package engine.graphics.glglfwImplementation.textures;

import engine.graphics.display.Window;
import engine.graphics.glglfwImplementation.guis.GuiTexture;
import engine.graphics.particles.ParticleTexture;
import engine.graphics.textures.Texture;
import engine.graphics.textures.TextureLoader;
import engine.toolbox.Log;
import org.joml.Vector2f;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.EXTTextureFilterAnisotropic;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL14;
import org.lwjgl.opengl.GL30;
import org.lwjgl.stb.STBImage;

import java.io.FileNotFoundException;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

import static engine.toolbox.Settings.ANISOTROPIC_FILTERING;
import static org.lwjgl.opengl.GL11.GL_LINEAR;
import static org.lwjgl.opengl.GL11.GL_RGBA8;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_MIN_FILTER;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_WRAP_S;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_WRAP_T;
import static org.lwjgl.opengl.GL11.GL_UNSIGNED_BYTE;
import static org.lwjgl.opengl.GL12.GL_CLAMP_TO_EDGE;
import static org.lwjgl.opengl.GL12.GL_TEXTURE_WRAP_R;
import static org.lwjgl.opengl.GL13.GL_TEXTURE_CUBE_MAP;

public class GLTextureLoader implements TextureLoader {
    private static final String TAG = "GLTextureLoader";
    private List<Integer> textures = new ArrayList<>();

    private static TextureData decodeTextureFile(String fileName, boolean flip) throws FileNotFoundException {
        IntBuffer h = BufferUtils.createIntBuffer(1);
        IntBuffer w = BufferUtils.createIntBuffer(1);
        IntBuffer comp = BufferUtils.createIntBuffer(1);
        STBImage.stbi_set_flip_vertically_on_load(flip);
        ByteBuffer image = STBImage.stbi_load(fileName, w, h, comp, 4);
        if (image == null) {
            throw new FileNotFoundException("Failed to load a texture file! (" + fileName + ")\n" + STBImage.stbi_failure_reason());
        }
        return new TextureData(image, w.get(), h.get());
    }

    /**
     * loads a texture file to VRAM
     *
     * @param fileName textures filename including extension
     * @param flip     if true flips the texture after loading
     * @return texture id
     */
    private int loadGLTexture(String fileName, boolean flip) {
        if (fileName == null) {
            fileName = "white.png";
            Log.w(TAG, "tried to load null texture");
        }
        TextureData data;
        try {
            data = decodeTextureFile("res/textures/" + fileName, flip);
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
            return -1;
        }
        int texID = GL11.glGenTextures();
        GL13.glActiveTexture(GL13.GL_TEXTURE0);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, texID);
        GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL_RGBA8, data.getWidth(), data.getHeight(), 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, data.getBuffer());
        //
        GL30.glGenerateMipmap(GL11.GL_TEXTURE_2D);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR_MIPMAP_LINEAR);
        GL11.glTexParameterf(GL11.GL_TEXTURE_2D, GL14.GL_TEXTURE_LOD_BIAS, -1.6f); //todo remove hardcode
        if (GL.getCapabilities().GL_EXT_texture_filter_anisotropic && ANISOTROPIC_FILTERING > 0) {
            GL11.glTexParameterf(GL11.GL_TEXTURE_2D, GL14.GL_TEXTURE_LOD_BIAS, 0); //todo !!?
            float amount = Math.min(ANISOTROPIC_FILTERING, GL11.glGetFloat(EXTTextureFilterAnisotropic.GL_MAX_TEXTURE_MAX_ANISOTROPY_EXT));
            GL11.glTexParameterf(GL11.GL_TEXTURE_2D, EXTTextureFilterAnisotropic.GL_TEXTURE_MAX_ANISOTROPY_EXT, amount);
        } else {
            Log.i("anisotropic filterring is not supported or disabled!");
        }
        textures.add(texID);
        Log.d(TAG, "texture 'res/textures/" + fileName + "' loaded");
        return texID;
    }

    private int loadFontTexture(String fileName) {
        TextureData data;
        try {
            data = decodeTextureFile("res/fonts/" + fileName, false);
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
            return 0;
        }
        int texID = GL11.glGenTextures();
        GL13.glActiveTexture(GL13.GL_TEXTURE0);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, texID);
        GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL_RGBA8, data.getWidth(), data.getHeight(), 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, data.getBuffer());

        GL30.glGenerateMipmap(GL11.GL_TEXTURE_2D);
        //GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER,GL11.GL_LINEAR_MIPMAP_LINEAR);
        //GL11.glTexParameterf(GL11.GL_TEXTURE_2D, GL14.GL_TEXTURE_LOD_BIAS, -0.6f); //todo remove hardcode
        textures.add(texID);
        return texID;
    }

    /**
     * loads a cubemap texture for skyboxes
     *
     * @param textureFiles  filenames without extensions, in order pos. X, neg. X, pos./neg. Y, pos./neg. Z
     * @param fileExtension filenames file extensions like ".png"
     * @return texture id
     */
    public int loadCubeMapTexture(String[] textureFiles, String fileExtension) {
        int texID = GL11.glGenTextures();
        GL13.glActiveTexture(GL13.GL_TEXTURE0);
        GL11.glBindTexture(GL_TEXTURE_CUBE_MAP, texID);
        for (int i = 0; i < textureFiles.length; i++) {
            TextureData data;
            try {
                data = decodeTextureFile("res/textures/" + textureFiles[i] + fileExtension, false);
            } catch (Exception e) {
                Log.e(TAG, e.getMessage());
                continue;
            }
            GL11.glTexImage2D(GL13.GL_TEXTURE_CUBE_MAP_POSITIVE_X + i, 0, GL11.GL_RGBA, data.getHeight(),
                    data.getWidth(), 0, GL11.GL_RGBA, GL_UNSIGNED_BYTE, data.getBuffer());
        }
        //posX, negX, posY, negY, posZ, negZ
        GL11.glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_MIN_FILTER, GL_LINEAR); //todo what does it tut27
        GL11.glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_WRAP_R, GL_CLAMP_TO_EDGE);
        GL11.glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
        GL11.glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
        textures.add(texID);
        return texID;
    }

    /**
     * loads a cubemap texture for skyboxes
     *
     * @param textureFile   filename without extensions and postfix, the actual textures must be named filename + "_rt"/"_lf"/"_up"/"_dn"/"_bk"/"_ft"
     * @param fileExtension filenames file extensions like ".png"
     * @return texture id
     */
    public int loadCubeMapTexture(String textureFile, String fileExtension) {
        String[] postfix = {"_rt", "_lf", "_up", "_dn", "_bk", "_ft"};
        String[] fileNames = new String[6];
        for (int i = 0; i < 6; i++) {
            fileNames[i] = textureFile + postfix[i];
        }
        return loadCubeMapTexture(fileNames, fileExtension);
    }

    @Override
    public Texture loadTexture(String name) {
        return new GLModelTexture(loadGLTexture(name, true));
    }

    /**
     * loads a texture file to VRAM
     *
     * @param fileName textures filename including extension
     * @return loaded texture
     */
    @Override
    public Texture loadTexture(String fileName, boolean flip) {
        return new GLModelTexture(loadGLTexture(fileName, flip));
    }

    @Override
    public GuiTexture loadGuiTexture(String name, Vector2f position, Vector2f scale, Window window) {
        return new GuiTexture(loadGLTexture(name, true),position,scale,window);
    }

    @Override
    public TerrainTexture loadTerrainTexture(String file) {
        return new TerrainTexture(loadGLTexture(file, true));
    }

    @Override
    public ParticleTexture loadParticleTexture(String file, boolean flip, int numberOfRows, boolean isAdditive, boolean randomizeAtlas) {
        return new ParticleTexture(loadGLTexture(file, flip), numberOfRows, isAdditive, randomizeAtlas);
    }

    @Override
    public ParticleTexture loadParticleTexture(String file, int numberOfRows, boolean isAdditive, boolean randomizeAtlas) {
        return new ParticleTexture(loadGLTexture(file, true), numberOfRows, isAdditive, randomizeAtlas);
    }

    public void cleanUp() {
        textures.forEach(GL11::glDeleteTextures);
    }
}
