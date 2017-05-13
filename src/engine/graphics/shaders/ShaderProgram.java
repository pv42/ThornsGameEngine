package engine.graphics.shaders;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.util.vector.*;
import engine.toolbox.Log;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.FloatBuffer;

/**
 * Created by pv42 on 17.06.16.
 */
public abstract class ShaderProgram {
    private int programID;
    private int vertexShaderID;
    private int fragmentShaderID;
    private static FloatBuffer matrixBuffer = BufferUtils.createFloatBuffer(16);

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


    public void start() {
        GL20.glUseProgram(programID);
    }
    public void stop() {
        GL20.glUseProgram(0);
    }

    public void cleanUp() {
        stop();
        GL20.glDetachShader(programID, vertexShaderID);
        GL20.glDetachShader(programID, fragmentShaderID);
        GL20.glDeleteShader(vertexShaderID);
        GL20.glDeleteShader(fragmentShaderID);
        GL20.glDeleteProgram(programID);
    }

    /**
     * binds vbos to names
     */
    protected abstract void bindAttributes();

    /**
     * bind a vbo to a var name in the shader
     * @param attribute vbo number in vao
     * @param variableName var name in glsl shader code
     */
    protected void bindAttribute(int attribute, String variableName) {
        GL20.glBindAttribLocation(programID, attribute, variableName);
    }

    protected abstract void getAllUniformLocations();

    public int getUniformLocation(String uniformName) {
        return GL20.glGetUniformLocation(programID,uniformName);
    }

    /**
     * loads a binary value to a shaders uniform
     * @param location uniform location
     * @param value binary value to load
     */
    protected void loadBoolean(int location, boolean value) {
        float toLoad = 0;
        if(value) toLoad = 1;
        loadFloat(location,toLoad);
    }

    /**
     * loads a integer value to a shaders uniform
     * @param location uniform location
     * @param value int value to load
     */
    protected void loadInt(int location, int value) {
        GL20.glUniform1i(location,value);
    }
    protected void loadFloat(int location , float value) {
        GL20.glUniform1f(location,value);
    }
    protected void loadVector(int loaction, Vector2f vector) {
        GL20.glUniform2f(loaction,vector.getX(),vector.getY());
    }
    protected void loadVector(int loaction, Vector3f vector) {
        GL20.glUniform3f(loaction,vector.getX(),vector.getY(),vector.getZ());
    }
    protected void loadVector(int loaction, Vector4f vector) {
        GL20.glUniform4f(loaction,vector.getX(),vector.getY(),vector.getZ(),vector.getW());
    }
    protected void loadMatrix(int location, Matrix4f matrix) {
        matrix.store(matrixBuffer);
        matrixBuffer.flip();
        GL20.glUniformMatrix4fv(location,false,matrixBuffer);
    }

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
            Log.e("failed loading shader from '" + file + "' exiting");
            System.exit(-1);
        }
        int shaderID = GL20.glCreateShader(type);
        GL20.glShaderSource(shaderID, shaderSource);
        GL20.glCompileShader(shaderID);
        if (GL20.glGetShaderi(shaderID, GL20.GL_COMPILE_STATUS) == GL11.GL_FALSE) {

            Log.e("Could not compile shader : '" + file + "':");
            System.out.println(GL20.glGetShaderInfoLog(shaderID, 500));
            System.exit(-1);
        }
        return shaderID;
    }
}
