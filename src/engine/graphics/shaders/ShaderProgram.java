package engine.graphics.shaders;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.joml.*;
import engine.toolbox.Log;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

import static org.lwjgl.opengl.GL11.GL_TRUE;
import static org.lwjgl.opengl.GL20.GL_VALIDATE_STATUS;

/**
 * A openGL shader program with vertex and fragment shader
 * Created on 17.06.16.
 * @author pv42
 */
public abstract class ShaderProgram {
    private static final String SHADER_LOCATION = "src/engine/graphics/shaders/glsl/";
    private static final Object SHADER_NAME_EXTENSION = ".glsl";
    private int programID;
    private int vertexShaderID;
    private int fragmentShaderID;
    private static float[] matrixBuffer = new float[16];

    /**
     * creates a shader program from vertex and fragment file
     * @param vertexShaderName vertex file name
     * @param fragmentShaderName fragment file name
     */
    public ShaderProgram(String vertexShaderName, String fragmentShaderName) {
        vertexShaderID = loadShader(vertexShaderName, GL20.GL_VERTEX_SHADER);
        fragmentShaderID = loadShader(fragmentShaderName, GL20.GL_FRAGMENT_SHADER);
        programID = GL20.glCreateProgram();
        GL20.glAttachShader(programID, vertexShaderID);
        GL20.glAttachShader(programID, fragmentShaderID);
        bindAttributes();
        GL20.glLinkProgram(programID);
        GL20.glValidateProgram(programID);
        int validate = GL20.glGetProgrami(programID,GL_VALIDATE_STATUS);
        if(validate != GL_TRUE) Log.e("shader could not be validated");
        getAllUniformLocations();
    }

    /**
     * enables the shader to be applied
     */
    public void start() {
        GL20.glUseProgram(programID);
    }

    /**
     * disables the shader (and any other shader)
     */
    public void stop() {
        GL20.glUseProgram(0);
    }

    /**
     * unloads the shader from VRAM
     */
    public void cleanUp() {
        stop();
        GL20.glDetachShader(programID, vertexShaderID);
        GL20.glDetachShader(programID, fragmentShaderID);
        GL20.glDeleteShader(vertexShaderID);
        GL20.glDeleteShader(fragmentShaderID);
        GL20.glDeleteProgram(programID);
    }

    /**
     * binds all vbos to names in the shader
     */
    protected abstract void bindAttributes();

    /**
     * bind a vbo to a var name in the shader
     *
     * @param attribute    vbo number in vao
     * @param variableName var name in glsl shader code
     */
    protected void bindAttribute(int attribute, String variableName) {
        GL20.glBindAttribLocation(programID, attribute, variableName);
    }

    /**
     * gets all shader uniform locations and stores them
     */
    protected abstract void getAllUniformLocations();

    /**
     * get a shader's uniform location in the vram
     * @param uniformName the uniform's variable name
     * @return the uniform location in vram
     */
    public int getUniformLocation(String uniformName) {
        return GL20.glGetUniformLocation(programID, uniformName);
    }

    /**
     * loads a binary value to a shader's uniform
     *
     * @param location uniform location
     * @param value    binary value to load
     */
    protected void loadBoolean(int location, boolean value) {
        float toLoad = 0;
        if (value) toLoad = 1;
        loadFloat(location, toLoad);
    }

    /**
     * loads a integer value to a shaders uniform
     *
     * @param location uniform location
     * @param value    int value to load
     */
    protected void loadInt(int location, int value) {
        GL20.glUniform1i(location, value);
    }

    /**
     * loads a float value to a shader's uniform
     *
     * @param location uniform location
     * @param value    float value to load
     */
    protected void loadFloat(int location, float value) {
        GL20.glUniform1f(location, value);
    }

    /**
     * loads a 2D-float-vector to a shader's uniform
     * @param location uniform location
     * @param vector vector to load
     */
    protected void loadVector(int location, Vector2f vector) {
        GL20.glUniform2f(location, vector.x(), vector.y());
    }

    /**
     * loads a 3D-float-vector to a shader's uniform
     * @param location uniform location
     * @param vector vector to load
     */
    protected void loadVector(int location, Vector3f vector) {
        GL20.glUniform3f(location, vector.x(), vector.y(), vector.z());
    }

    /**
     * loads a 4D-float-vector to a shader's uniform
     * @param location uniform location
     * @param vector vector to load
     */
    protected void loadVector(int location, Vector4f vector) {
        GL20.glUniform4f(location, vector.x(), vector.y(), vector.z(), vector.w());
    }

    /**
     * loads a 4x4-float-matrix to a shader's uniform
     * @param location uniform location
     * @param matrix matrix to load
     */
    protected void loadMatrix(int location, Matrix4f matrix) {
        matrix.get(matrixBuffer);
        GL20.glUniformMatrix4fv(location, false, matrixBuffer);
    }

    /**
     * loads a list of vectors to a array of vectors uniform in the VRAM
     * @param location uniform location in the VRAM
     * @param vectors vectors to load
     * @param size uniform array size, max amount of vectors to load
     */
    protected void loadVectorArray(int location, List<Vector3f> vectors, int size) {
        float data[] = new float[size*3];
        for(int i = 0; i < size; i++) {
            if (i < vectors.size()) {
                data[3 * i] = vectors.get(i).x();
                data[3 * i + 1] = vectors.get(i).y();
                data[3 * i + 2] = vectors.get(i).z();
            } else {
                data[3 * i] = 0;
                data[3 * i + 1] = 0;
                data[3 * i + 2] = 0;

            }
        }
        GL20.glUniform3fv(location, data);
    }

    /**
     * loads a list of matrices to a array of matrices uniform in the VRAM
     * @param location uniform location in the VRAM
     * @param matrices matrices to load
     * @param size uniform array size, max amount of matrices to load
     */
    protected void loadMatrixArray(int location, List<Matrix4f> matrices, int size) {
        float data[] = new float[size * 16];
        for(int i = 0; i < size; i++) {
            if(i < matrices.size()) {
                matrices.get(i).get(data,i * 16);
            }
        }
        GL20.glUniformMatrix4fv(location, false, data);
    }

    /**
     * compiles and loads a glsl shader
     * @param shaderName shader's source file path
     * @param type shader type e.g. vertex or fragment
     * @return the shaderID or 0 if the loading failed
     */
    private static int loadShader(String shaderName, int type) {
        StringBuilder shaderSource = new StringBuilder();
        try {
            BufferedReader reader = new BufferedReader(new FileReader(SHADER_LOCATION + shaderName + SHADER_NAME_EXTENSION));
            String line;
            while ((line = reader.readLine()) != null) {
                shaderSource.append(line).append("//\n");
            }
            reader.close();
        } catch (IOException e) {
            Log.e("failed loading shader from '" + shaderName + "'");
            return 0;
        }
        int shaderID = GL20.glCreateShader(type);
        GL20.glShaderSource(shaderID, shaderSource);
        GL20.glCompileShader(shaderID);
        if (GL20.glGetShaderi(shaderID, GL20.GL_COMPILE_STATUS) == GL11.GL_FALSE) {
            Log.e("Could not compile shader : '" + shaderName + "':");
            System.out.println(GL20.glGetShaderInfoLog(shaderID, 500));
            return 0;
        }
        Log.d("shader '" + shaderName + "' loaded");
        return shaderID;
    }


}
