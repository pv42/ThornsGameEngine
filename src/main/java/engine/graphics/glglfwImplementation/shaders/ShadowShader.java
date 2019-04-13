package engine.graphics.glglfwImplementation.shaders;

import org.joml.Matrix4f;

import java.util.List;

import static engine.graphics.glglfwImplementation.GLLoader.VERTEX_ATTRIB_ARRAY_BONE_INDICES;
import static engine.graphics.glglfwImplementation.GLLoader.VERTEX_ATTRIB_ARRAY_BONE_WEIGHT;
import static engine.graphics.glglfwImplementation.GLLoader.VERTEX_ATTRIB_ARRAY_POSITION;
import static engine.toolbox.Settings.MAX_BONES;

public class ShadowShader extends ShaderProgram {
    private static final String FRAGMENT = "shadowFragmentShader";
    private static final String VERTEX = "shadowVertexShader";

    private int location_mvpMatrix;
    private int location_bones;
    private int location_useAnimation;

    public ShadowShader() {
        super(VERTEX, FRAGMENT);
    }

    @Override
    protected void bindAttributes() {
        super.bindAttribute(VERTEX_ATTRIB_ARRAY_POSITION, "position");
        super.bindAttribute(VERTEX_ATTRIB_ARRAY_BONE_INDICES, "bone_index"); //only used in animation
        super.bindAttribute(VERTEX_ATTRIB_ARRAY_BONE_WEIGHT, "bone_weight"); //same
    }

    @Override
    protected void getAllUniformLocations() {
        location_mvpMatrix = super.getUniformLocation("mvpMatrix");
        location_bones = super.getUniformLocation("bone");
        location_useAnimation = super.getUniformLocation("useAnimation");
    }

    public void loadMVPMatrix(Matrix4f mvp) {
        super.loadMatrix(location_mvpMatrix, mvp);
    }
    /**
     * load the bone matrices into the shader uniforms
     *
     * @param bones bone matrices to load
     */
    public void loadBones(List<Matrix4f> bones) {
        super.loadMatrixArray(location_bones, bones, MAX_BONES);
    }

    /**
     * loads whether the rendered gpu should use bone animation
     *
     * @param useAnimation should animations be used
     */
    public void loadUseAnimation(boolean useAnimation) {
        super.loadBoolean(location_useAnimation, useAnimation);
    }
}
