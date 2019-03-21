package engine.graphics.renderEngine;

import engine.graphics.animation.Joint;
import engine.graphics.lines.Line;
import engine.graphics.lines.LineModel;
import engine.graphics.models.RawModel;
import engine.graphics.textures.TextureData;
import engine.toolbox.Log;
import engine.toolbox.Settings;
import engine.toolbox.StorageFormatUtil;
import org.joml.Vector3f;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.EXTTextureFilterAnisotropic;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL14;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.GL33;
import org.lwjgl.stb.STBImage;

import java.io.FileNotFoundException;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.opengl.GL11.GL_LINEAR;
import static org.lwjgl.opengl.GL11.GL_RGBA8;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_MIN_FILTER;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_WRAP_S;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_WRAP_T;
import static org.lwjgl.opengl.GL11.GL_UNSIGNED_BYTE;
import static org.lwjgl.opengl.GL12.GL_CLAMP_TO_EDGE;
import static org.lwjgl.opengl.GL12.GL_TEXTURE_WRAP_R;
import static org.lwjgl.opengl.GL13.GL_TEXTURE_CUBE_MAP;

/***
 * Created by pv42 on 16.06.16.
 */
public class Loader {
    public static final int VERTEX_ATTRIB_ARRAY_POSITION = 0;
    public static final int VERTEX_ATTRIB_ARRAY_UV = 1;
    public static final int VERTEX_ATTRIB_ARRAY_NORMAL = 2;
    public static final int VERTEX_ATTRIB_ARRAY_BONE_INDICES = 3; // todo both tangents and animation
    public static final int VERTEX_ATTRIB_ARRAY_BONE_WEIGHT = 4;
    static final int VERTEX_ATTRIB_ARRAY_TANGENTS = 3;
    private static final float ANISOTROPIC_FILTERING = Settings.ANISOTROPIC_FILTERING;
    private static final String TAG = "Loader";
    private static List<Integer> vaos = new ArrayList<>();
    private static List<Integer> vbos = new ArrayList<>();
    private static List<Integer> textures = new ArrayList<>();

    /**
     * loads a rawmodel with only a position array in 2D or 3D
     *
     * @param positions positions array
     * @param dimension coordinate dimension
     * @return rawmodel
     */
    public static RawModel loadToVAO(float[] positions, int dimension) {
        int vaoID = createVAO();
        storeDataInAttributeList(VERTEX_ATTRIB_ARRAY_POSITION, dimension, positions);
        unbindVAO();
        return new RawModel(vaoID, positions.length / dimension);
    }

    /**
     * loads a rawmodel with positions and indices
     *
     * @param positions positions array
     * @param indices   indices array
     * @return rawmodel
     */
    public static RawModel loadToVAO(float[] positions, int[] indices) {
        int vaoID = createVAO();
        bindIndicesBuffer(indices);
        storeDataInAttributeList(VERTEX_ATTRIB_ARRAY_POSITION, 3, positions);
        unbindVAO();
        return new RawModel(vaoID, indices.length);
    }

    /**
     * loads a rawmodel with positions and indices
     *
     * @param positions positions array
     * @param uv        texture coordinates array
     * @return rawmodel
     */
    public static int loadToVAO(float[] positions, float[] uv) {
        int vaoID = createVAO();
        storeDataInAttributeList(VERTEX_ATTRIB_ARRAY_POSITION, 2, positions);
        storeDataInAttributeList(VERTEX_ATTRIB_ARRAY_UV, 2, uv);
        unbindVAO();
        return vaoID;
    }

    /**
     * loads a rawmodel with positions, texture coordinates and indices
     *
     * @param positions positions array
     * @param uv        texture coordinates array
     * @param indices   indices array
     * @return rawmodel
     */
    public static RawModel loadToVAO(float[] positions, float[] uv, int[] indices) {
        int vaoID = createVAO();
        bindIndicesBuffer(indices);
        storeDataInAttributeList(VERTEX_ATTRIB_ARRAY_POSITION, 3, positions);
        storeDataInAttributeList(VERTEX_ATTRIB_ARRAY_UV, 2, uv);
        unbindVAO();
        return new RawModel(vaoID, indices.length);
    }

    /**
     * loads a rawmodel with positions, texture coordinates, normals and indices
     *
     * @param positions positions array
     * @param uv        texture coordinates array
     * @param normals   surface normal array
     * @param indices   indices array
     * @return rawmodel
     */
    public static RawModel loadToVAO(float[] positions, float[] uv, float[] normals, int[] indices) {
        int vaoID = createTexturedLightedVAO(positions, uv, normals, indices);
        unbindVAO();
        return new RawModel(vaoID, indices.length);
    }

    /**
     * loads a rawmodel with positions, texture coordinates, normals, tangents and indices
     *
     * @param positions positions array
     * @param uv        texture coordinates array
     * @param normals   surface normal array
     * @param tangents  surface tangents array
     * @param indices   indices array
     * @return rawmodel
     */
    public static RawModel loadToVAO(float[] positions, float[] uv, float[] normals, float[] tangents, int[] indices) {
        int vaoID = createTexturedLightedVAO(positions, uv, normals, indices);
        storeDataInAttributeList(VERTEX_ATTRIB_ARRAY_TANGENTS, 3, tangents);
        unbindVAO();
        return new RawModel(vaoID, indices.length);
    }

    public static LineModel loadToVAO(Line line) {
        int vaoID = createVAO();
        int[] indices = new int[2];
        for (int i = 0; i < 2; i++) {
            indices[i] = i;
        }
        bindIndicesBuffer(indices);
        storeDataInAttributeList(0, 3, StorageFormatUtil.get1DArray(new Vector3f[]{line.getPoint1(), line.getPoint2()}));
        unbindVAO();
        return new LineModel(vaoID, line.getColor(), 2);
    }

    /**
     * loads a animated rawmodel with positions, texture coordinates, normals, boneIndices, boneWeights and indices
     *
     * @param positions   positions array
     * @param uv          texture coordinates array
     * @param normals     surface normal array
     * @param boneIndices bone indices
     * @param boneWeight  bone weights for the indexed bones
     * @param indices     indices array
     * @param joints      list of the joints/bones to use
     * @return rawmodel
     */
    public static RawModel loadToVAOAnimated(float[] positions, float[] uv, float[] normals, int[] indices, int[] boneIndices, float[] boneWeight, List<Joint> joints) {
        int vaoID = createTexturedLightedVAO(positions, uv, normals, indices);
        storeDataInAttributeList(VERTEX_ATTRIB_ARRAY_BONE_INDICES, Settings.MAX_BONES_PER_VERTEX, boneIndices);
        storeDataInAttributeList(VERTEX_ATTRIB_ARRAY_BONE_WEIGHT, Settings.MAX_BONES_PER_VERTEX, boneWeight);
        unbindVAO();
        return new RawModel(vaoID, indices.length, joints);
    }

    private static int createTexturedLightedVAO(float[] positions, float[] uv, float[] normals, int[] indices) {
        int vaoID = createVAO();
        bindIndicesBuffer(indices);
        storeDataInAttributeList(VERTEX_ATTRIB_ARRAY_POSITION, 3, positions);
        storeDataInAttributeList(VERTEX_ATTRIB_ARRAY_UV, 2, uv);
        storeDataInAttributeList(VERTEX_ATTRIB_ARRAY_NORMAL, 3, normals);
        return vaoID;
    }

    public static int createEmptyVbo(int floatCount) {
        int vbo = GL15.glGenBuffers();
        vbos.add(vbo);
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vbo);
        GL15.glBufferData(GL15.GL_ARRAY_BUFFER, floatCount * 4, GL15.GL_STREAM_DRAW);
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
        return vbo;

    }

    public static void addInstancesAttribute(int vao, int vbo, int attribute, int dataSize, int instancedDataLenght, int offset) {
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vbo);
        GL30.glBindVertexArray(vao);
        GL20.glVertexAttribPointer(attribute, dataSize, GL11.GL_FLOAT, false, instancedDataLenght * 4, offset * 4);
        GL33.glVertexAttribDivisor(attribute, 1);
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
        GL30.glBindVertexArray(0);

    }

    public static void updateVbo(int vbo, float[] data, FloatBuffer buffer) {
        buffer.clear();
        buffer.put(data);
        buffer.flip();
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vbo);
        GL15.glBufferData(GL15.GL_ARRAY_BUFFER, buffer.capacity() * 4, GL15.GL_STATIC_DRAW);
        GL15.glBufferSubData(GL15.GL_ARRAY_BUFFER, 0, buffer);
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
    }

    /**
     * loads a texture file to VRAM
     *
     * @param fileName textures filename including extension
     * @return texture id
     */
    public static int loadTexture(String fileName) {
        return loadTexture(fileName, true);
    }

    /**
     * loads a texture file to VRAM
     *
     * @param fileName textures filename including extension
     * @param flip     if true flips the texture after loading
     * @return texture id
     */
    public static int loadTexture(String fileName, boolean flip) {
        if (fileName == null) {
            fileName = "white.png";
            Log.w(TAG, "tried to load null texture");
        }
        TextureData data;
        try {
            data = decodeTextureFile("res/textures/" + fileName, flip);
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
            return 0;
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

    private static int loadFontTexture(String fileName) {
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
    public static int loadCubeMapTexture(String[] textureFiles, String fileExtension) {
        int texID = GL11.glGenTextures();
        GL13.glActiveTexture(GL13.GL_TEXTURE0);
        GL11.glBindTexture(GL_TEXTURE_CUBE_MAP, texID);
        for (int i = 0; i < textureFiles.length; i++) {
            TextureData date = null;
            try {
                date = decodeTextureFile("res/textures/" + textureFiles[i] + fileExtension, false);
            } catch (Exception e) {
                Log.e(TAG, e.getMessage());
            }
            GL11.glTexImage2D(GL13.GL_TEXTURE_CUBE_MAP_POSITIVE_X + i, 0, GL11.GL_RGBA, date.getHeight(), date.getWidth(), 0, GL11.GL_RGBA, GL_UNSIGNED_BYTE, date.getBuffer());
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
    public static int loadCubeMapTexture(String textureFile, String fileExtension) {
        String[] postfix = {"_rt", "_lf", "_up", "_dn", "_bk", "_ft"};
        String[] fileNames = new String[6];
        for (int i = 0; i < 6; i++) {
            fileNames[i] = textureFile + postfix[i];
        }
        return loadCubeMapTexture(fileNames, fileExtension);
    }

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
     * creates and binds a vertex array object
     *
     * @return vao id
     */
    private static int createVAO() {
        int vaoID = GL30.glGenVertexArrays();
        vaos.add(vaoID);
        GL30.glBindVertexArray(vaoID);
        return vaoID;
    }

    private static void storeDataInAttributeList(int attributeNumber, int coordinateSize, float[] data) {
        int vboID = GL15.glGenBuffers();
        vbos.add(vboID);
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vboID);
        FloatBuffer floatBuffer = storeDataInFloatBuffer(data);
        GL15.glBufferData(GL15.GL_ARRAY_BUFFER, floatBuffer, GL15.GL_STATIC_DRAW);
        GL20.glVertexAttribPointer(attributeNumber, coordinateSize, GL11.GL_FLOAT, false, 0, 0);
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
    }

    private static void storeDataInAttributeList(int attributeNumber, int coordinateSize, int[] data) {
        int vboID = GL15.glGenBuffers();
        vbos.add(vboID);
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vboID);
        IntBuffer intBuffer = storeDataInIntBuffer(data);
        GL15.glBufferData(GL15.GL_ARRAY_BUFFER, intBuffer, GL15.GL_STATIC_DRAW);
        GL20.glVertexAttribPointer(attributeNumber, coordinateSize, GL11.GL_INT, false, 0, 0);
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
    }

    /**
     * unbinds any vaos
     */
    private static void unbindVAO() {
        GL30.glBindVertexArray(0);
    }

    private static void bindIndicesBuffer(int[] indices) {
        int vboID = GL15.glGenBuffers();
        vbos.add(vboID);
        GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, vboID);
        IntBuffer buffer = storeDataInIntBuffer(indices);
        GL15.glBufferData(GL15.GL_ELEMENT_ARRAY_BUFFER, buffer, GL15.GL_STATIC_DRAW);
    }

    private static IntBuffer storeDataInIntBuffer(int[] data) {
        IntBuffer buffer = BufferUtils.createIntBuffer(data.length);
        buffer.put(data);
        buffer.flip();
        return buffer;
    }

    private static FloatBuffer storeDataInFloatBuffer(float[] data) {
        FloatBuffer floatBuffer = BufferUtils.createFloatBuffer(data.length);
        floatBuffer.put(data);
        floatBuffer.flip();
        return floatBuffer;
    }

    /**
     * unloads all textures, vaos and vbos from VRAM
     */
    public static void cleanUp() {
        vbos.forEach(GL15::glDeleteBuffers);
        vaos.forEach(GL30::glDeleteVertexArrays);
        textures.forEach(GL11::glDeleteTextures);
    }
}
