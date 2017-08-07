package engine.graphics.renderEngine;

import engine.graphics.animation.Bone;
import engine.graphics.fontMeshCreator.FontType;
import engine.graphics.lines.Line;
import engine.graphics.lines.LineModel;
import engine.graphics.models.RawModel;
import engine.toolbox.*;
import engine.toolbox.Util;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.*;
import org.lwjgl.stb.STBImage;
import org.joml.Vector3f;
import engine.graphics.textures.TextureData;

import java.io.File;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

/***
 * Created by pv42 on 16.06.16.
 */
public class Loader {
    static final int VERTEX_ATTRIB_ARRAY_POSITION = 0;
    static final int VERTEX_ATTRIB_ARRAY_UV = 1;
    static final int VERTEX_ATTRIB_ARRAY_NORMAL = 2;
    static final int VERTEX_ATTRIB_ARRAY_BONEINDICES = 3;
    static final int VERTEX_ATTRIB_ARRAY_BONEWEIGHT = 4;
    private static final float ANISOTROPIC_FILTERING = Settings.ANISOTROPIC_FILTERING;
    private static List<Integer> vaos = new ArrayList<>();
    private static List<Integer> vbos = new ArrayList<>();
    private static List<Integer> textures = new ArrayList<>();

    public static RawModel loadToVAO(float[] positions, int dimension) {
        int vaoID = createVAO();
        storeDataInAttributeList(VERTEX_ATTRIB_ARRAY_POSITION, dimension, positions);
        unbindVAO();
        return new RawModel(vaoID, positions.length / dimension);
    }

    public static RawModel loadToVAO(float[] positions, int[] indices) {
        int vaoID = createVAO();
        bindIndicesBuffer(indices);
        storeDataInAttributeList(VERTEX_ATTRIB_ARRAY_POSITION, 3, positions);
        unbindVAO();
        return new RawModel(vaoID, indices.length);
    }

    public static RawModel loadToVAO(float[] positions, float[] uv, int[] indices) {
        int vaoID = createVAO();
        bindIndicesBuffer(indices);
        storeDataInAttributeList(VERTEX_ATTRIB_ARRAY_POSITION, 3, positions);
        storeDataInAttributeList(VERTEX_ATTRIB_ARRAY_UV, 2, uv);
        unbindVAO();
        return new RawModel(vaoID, indices.length);
    }

    public static RawModel loadToVAO(float[] positions, float[] uv, float[] normals, int[] indices) {
        int vaoID = createVAO();
        bindIndicesBuffer(indices);
        storeDataInAttributeList(VERTEX_ATTRIB_ARRAY_POSITION, 3, positions);
        storeDataInAttributeList(VERTEX_ATTRIB_ARRAY_UV, 2, uv);
        storeDataInAttributeList(VERTEX_ATTRIB_ARRAY_NORMAL, 3, normals);
        unbindVAO();
        return new RawModel(vaoID, indices.length);
    }

    public static RawModel loadToVAO(float[] positions, float[] uv, float[] normals, float tangents[], int[] indices) {
        int vaoID = createVAO();
        bindIndicesBuffer(indices);
        storeDataInAttributeList(VERTEX_ATTRIB_ARRAY_POSITION, 3, positions);
        storeDataInAttributeList(VERTEX_ATTRIB_ARRAY_UV, 2, uv);
        storeDataInAttributeList(VERTEX_ATTRIB_ARRAY_NORMAL, 3, normals);
        storeDataInAttributeList(3, 3, tangents);
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
        storeDataInAttributeList(0, 3, Util.get1DArray(new Vector3f[]{line.getPoint1(), line.getPoint2()}));
        unbindVAO();
        return new LineModel(vaoID, line.getColor(), 2);
    }

    public static RawModel loadToVAOAnimated(float[] positions, float[] uv, float[] normals, int[] indices, int[] boneIndices, float[] boneWeight, List<Bone> bones) {
        int vaoID = createVAO();
        bindIndicesBuffer(indices);
        storeDataInAttributeList(VERTEX_ATTRIB_ARRAY_POSITION, 3, positions);
        storeDataInAttributeList(VERTEX_ATTRIB_ARRAY_UV, 2, uv);
        storeDataInAttributeList(VERTEX_ATTRIB_ARRAY_NORMAL, 3, normals);
        storeDataInAttributeList(VERTEX_ATTRIB_ARRAY_BONEINDICES, Settings.MAX_BONES_PER_VERTEX, boneIndices);
        storeDataInAttributeList(VERTEX_ATTRIB_ARRAY_BONEWEIGHT, Settings.MAX_BONES_PER_VERTEX, boneWeight);
        unbindVAO();
        return new RawModel(vaoID, indices.length, bones);
    }

    public static int loadToVAO(float[] positions, float[] uv) {
        int vaoID = createVAO();
        storeDataInAttributeList(VERTEX_ATTRIB_ARRAY_POSITION, 2, positions);
        storeDataInAttributeList(VERTEX_ATTRIB_ARRAY_UV, 2, uv);
        unbindVAO();
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

    public static int loadTexture(String fileName) {
        TextureData data = decodeTextureFile("res/textures/" + fileName, true);
        int texID = GL11.glGenTextures();
        GL13.glActiveTexture(GL13.GL_TEXTURE0);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, texID);
        GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA8, data.getWidth(), data.getHeight(), 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, data.getBuffer());
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
        return texID;
    }

    private static int loadFontTexture(String fileName) {
        TextureData data = decodeTextureFile("res/fonts/" + fileName, false);
        int texID = GL11.glGenTextures();
        GL13.glActiveTexture(GL13.GL_TEXTURE0);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, texID);
        GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA8, data.getWidth(), data.getHeight(), 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, data.getBuffer());

        GL30.glGenerateMipmap(GL11.GL_TEXTURE_2D);
        //GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER,GL11.GL_LINEAR_MIPMAP_LINEAR);
        //GL11.glTexParameterf(GL11.GL_TEXTURE_2D, GL14.GL_TEXTURE_LOD_BIAS, -0.6f); //todo remove hardcode
        textures.add(texID);
        return texID;
    }

    public static int loadCubeMapTexture(String[] textureFiles) {
        int texID = GL11.glGenTextures();
        GL13.glActiveTexture(GL13.GL_TEXTURE0);
        GL11.glBindTexture(GL13.GL_TEXTURE_CUBE_MAP, texID);
        for (int i = 0; i < textureFiles.length; i++) {
            TextureData date = decodeTextureFile("res/textures/" + textureFiles[i] + ".png", true);
            GL11.glTexImage2D(GL13.GL_TEXTURE_CUBE_MAP_POSITIVE_X + i, 0, GL11.GL_RGBA, date.getHeight(), date.getWidth(), 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, date.getBuffer());
        }
        //posX, negX, posY, negY, posZ, negZ
        GL11.glTexParameteri(GL13.GL_TEXTURE_CUBE_MAP, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
        GL11.glTexParameteri(GL13.GL_TEXTURE_CUBE_MAP, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR); //todo what does it tut27
        textures.add(texID);
        return texID;
    }

    public static int loadCubeMapTexture(String textureFile) {
        String[] postfix = {"_rt", "_lf", "_up", "_dn", "_bk", "_ft"};
        String[] fileNames = new String[6];
        for (int i = 0; i < 6; i++) {
            fileNames[i] = textureFile + postfix[i];
        }
        return loadCubeMapTexture(fileNames);
    }

    public static FontType loadFont(String fontName) {
        return new FontType(loadFontTexture(fontName + ".png"), new File("res/fonts/" + fontName + ".fnt"));
    }

    private static TextureData decodeTextureFile(String fileName, boolean flip) {
        IntBuffer h = BufferUtils.createIntBuffer(1);
        IntBuffer w = BufferUtils.createIntBuffer(1);
        IntBuffer comp = BufferUtils.createIntBuffer(1);
        STBImage.stbi_set_flip_vertically_on_load(flip);
        ByteBuffer image = STBImage.stbi_load(fileName, w, h, comp, 4);
        if (image == null) {
            throw new RuntimeException("Failed to load a texture file! (" + fileName + ")\n" + STBImage.stbi_failure_reason());
        }
        return new TextureData(image, w.get(), h.get());
    }

    private static int createVAO() {
        int vaoID = GL30.glGenVertexArrays();
        vaos.add(vaoID);
        GL30.glBindVertexArray(vaoID);
        return vaoID;
    }

    private static void storeDataInAttributeList(int attributeNumber, int coordianteSize, float[] data) {
        int vboID = GL15.glGenBuffers();
        vbos.add(vboID);
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vboID);
        FloatBuffer floatBuffer = storeDataInFloatBuffer(data);
        GL15.glBufferData(GL15.GL_ARRAY_BUFFER, floatBuffer, GL15.GL_STATIC_DRAW);
        GL20.glVertexAttribPointer(attributeNumber, coordianteSize, GL11.GL_FLOAT, false, 0, 0);
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
    }

    private static void storeDataInAttributeList(int attributeNumber, int coordianteSize, int[] data) {
        int vboID = GL15.glGenBuffers();
        vbos.add(vboID);
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vboID);
        IntBuffer intBuffer = storeDataInIntBuffer(data);
        GL15.glBufferData(GL15.GL_ARRAY_BUFFER, intBuffer, GL15.GL_STATIC_DRAW);
        GL20.glVertexAttribPointer(attributeNumber, coordianteSize, GL11.GL_INT, false, 0, 0);
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
    }

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

    public static void cleanUp() {
        vaos.forEach(GL30::glDeleteVertexArrays);
        vbos.forEach(GL15::glDeleteBuffers);
        textures.forEach(GL11::glDeleteTextures);
    }
}
