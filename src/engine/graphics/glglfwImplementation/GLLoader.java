package engine.graphics.glglfwImplementation;

import engine.graphics.animation.Joint;
import engine.graphics.glglfwImplementation.lines.Line;
import engine.graphics.glglfwImplementation.lines.LineModel;
import engine.graphics.glglfwImplementation.models.GLRawModel;
import engine.toolbox.Log;
import engine.toolbox.Settings;
import engine.toolbox.StorageFormatUtil;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.GL33;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

/***
 * Created by pv42 on 16.06.16.
 */
public class GLLoader {
    public static final int VERTEX_ATTRIB_ARRAY_POSITION = 0;
    public static final int VERTEX_ATTRIB_ARRAY_UV = 1;
    public static final int VERTEX_ATTRIB_ARRAY_NORMAL = 2;
    public static final int VERTEX_ATTRIB_ARRAY_BONE_INDICES = 3;
    public static final int VERTEX_ATTRIB_ARRAY_BONE_WEIGHT = 4;
    public static final int VERTEX_ATTRIB_ARRAY_TANGENTS = 5;
    private static final String TAG = "GLLoader";
    private static List<Integer> vaos = new ArrayList<>();
    private static List<Integer> vbos = new ArrayList<>();

    /**
     * loads a rawmodel with only a position array in 2D or 3D
     *
     * @param positions positions array
     * @param dimension coordinate dimension
     * @return rawmodel
     */
    @NotNull
    @Contract("_, _ -> new")
    public static GLRawModel loadToVAO(float[] positions, int dimension) {
        int vaoID = createVAO();
        storeDataInAttributeList(VERTEX_ATTRIB_ARRAY_POSITION, dimension, positions);
        unbindVAO();
        return new GLRawModel(vaoID, positions.length / dimension);
    }

    /**
     * loads a rawmodel with positions and indices
     *
     * @param positions positions array
     * @param indices   indices array
     * @return rawmodel
     */
    @NotNull
    @Contract("_, _ -> new")
    public static GLRawModel loadToVAO(float[] positions, int[] indices) {
        int vaoID = createVAO();
        bindIndicesBuffer(indices);
        storeDataInAttributeList(VERTEX_ATTRIB_ARRAY_POSITION, 3, positions);
        unbindVAO();
        return new GLRawModel(vaoID, indices.length);
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
    @NotNull
    @Contract("_, _, _ -> new")
    public static GLRawModel loadToVAO(float[] positions, float[] uv, int[] indices) {
        int vaoID = createVAO();
        bindIndicesBuffer(indices);
        storeDataInAttributeList(VERTEX_ATTRIB_ARRAY_POSITION, 3, positions);
        storeDataInAttributeList(VERTEX_ATTRIB_ARRAY_UV, 2, uv);
        unbindVAO();
        return new GLRawModel(vaoID, indices.length);
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
    @NotNull
    @Contract("_, _, _, _ -> new")
    public static GLRawModel loadToVAO(float[] positions, float[] uv, float[] normals, int[] indices) {
        int vaoID = createTexturedLightedVAO(positions, uv, normals, indices);
        unbindVAO();
        return new GLRawModel(vaoID, indices.length);
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
    @NotNull
    @Contract("_, _, _, _, _ -> new")
    public static GLRawModel loadToVAO(float[] positions, float[] uv, float[] normals, float[] tangents, int[] indices) {
        int vaoID = createTexturedLightedVAO(positions, uv, normals, indices);
        storeDataInAttributeList(VERTEX_ATTRIB_ARRAY_TANGENTS, 3, tangents);
        unbindVAO();
        return new GLRawModel(vaoID, indices.length);
    }

    @NotNull
    @Contract("_ -> new")
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
    @Contract("_, _, _, _, _, _, _ -> new")
    @NotNull
    public static GLRawModel loadToVAOAnimated(float[] positions, float[] uv, float[] normals, int[] indices, int[] boneIndices, float[] boneWeight, List<Joint> joints) {
        int vaoID = createTexturedLightedVAO(positions, uv, normals, indices);
        storeDataInAttributeList(VERTEX_ATTRIB_ARRAY_BONE_INDICES, Settings.MAX_BONES_PER_VERTEX, boneIndices);
        storeDataInAttributeList(VERTEX_ATTRIB_ARRAY_BONE_WEIGHT, Settings.MAX_BONES_PER_VERTEX, boneWeight);
        unbindVAO();
        return new GLRawModel(vaoID, indices.length, joints);
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

    public static void updateVbo(int vbo, float[] data, @NotNull FloatBuffer buffer) {
        buffer.clear();
        buffer.put(data);
        buffer.flip();
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vbo);
        GL15.glBufferData(GL15.GL_ARRAY_BUFFER, buffer.capacity() * 4, GL15.GL_STATIC_DRAW);
        GL15.glBufferSubData(GL15.GL_ARRAY_BUFFER, 0, buffer);
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
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

    private static IntBuffer storeDataInIntBuffer(@NotNull int[] data) {
        IntBuffer buffer = BufferUtils.createIntBuffer(data.length);
        buffer.put(data);
        buffer.flip();
        return buffer;
    }

    private static FloatBuffer storeDataInFloatBuffer(@NotNull float[] data) {
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
    }
}
