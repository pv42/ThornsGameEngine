package engine.graphics.shaders;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.joml.*;
import engine.toolbox.Log;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

/**
 * A openGL shader program with vertex and fragment shader
 * Created on 17.06.16.
 * @author pv42
 */
public abstract class ShaderProgram {
    private int programID;
    private int vertexShaderID;
    private int fragmentShaderID;
    private static float[] matrixBuffer = new float[16];

    /**
     * creates a shader program from vertex and fragment file
     * @param vertexFile vertex file location
     * @param fragmentFile fragment file location
     */
    public ShaderProgram(String vertexFile, String fragmentFile) {
        vertexShaderID = loadShader(vertexFile, GL20.GL_VERTEX_SHADER);
        fragmentShaderID = loadShader(fragmentFile, GL20.GL_FRAGMENT_SHADER);
        programID = GL20.glCreateProgram();
        GL20.glAttachShader(programID, vertexShaderID);
        GL20.glAttachShader(programID, fragmentShaderID);
        bindAttributes();
        GL20.glLinkProgram(programID);
        GL20.glValidateProgram(programID);
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
     * get a shader's array uniform locations in the vram
     * @param uniformName the uniform's variable name
     * @param locations array to store the locations
     * @param size arrays size
     */
    public void getUniformLocationsArray(String uniformName, int[] locations, int size) {
        for( int i = 0; i < size; i++) {
            locations[i] = getUniformLocation(uniformName + "[" + i + "]");
        }
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
     * compiles and loads a glsl shader
     * @param file shader's source file path
     * @param type shader type e.g. vertex or fragment
     * @return the shaderID or 0 if the loading failed
     */
    private static int loadShader(String file, int type) {
        StringBuilder shaderSource = new StringBuilder();
        try {
            BufferedReader reader = new BufferedReader(new FileReader(file));
            String line;
            while ((line = reader.readLine()) != null) {
                shaderSource.append(line).append("//\n");
            }
            reader.close();
        } catch (IOException e) {
            Log.e("failed loading shader from '" + file + "'");
            return 0;
        }
        int shaderID = GL20.glCreateShader(type);
        GL20.glShaderSource(shaderID, shaderSource);
        GL20.glCompileShader(shaderID);
        if (GL20.glGetShaderi(shaderID, GL20.GL_COMPILE_STATUS) == GL11.GL_FALSE) {
            Log.e("Could not compile shader : '" + file + "':");
            System.out.println(GL20.glGetShaderInfoLog(shaderID, 500));
            return 0;
        }
        Log.d("shader '" + file + "' loaded");
        return shaderID;
    }
}
