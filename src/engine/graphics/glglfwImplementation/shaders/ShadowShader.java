package engine.graphics.glglfwImplementation.shaders;

import engine.graphics.glglfwImplementation.GLLoader;
import org.joml.Matrix4f;

public class ShadowShader extends ShaderProgram {
    private static final String FRAGMENT = "shadowFragmentShader";
    private static final String VERTEX = "shadowVertexShader";

    private int location_mvpMatrix;

    public ShadowShader() {
        super(VERTEX, FRAGMENT);
    }

    @Override
    protected void bindAttributes() {
        super.bindAttribute(GLLoader.VERTEX_ATTRIB_ARRAY_POSITION, "position");
    }

    @Override
    protected void getAllUniformLocations() {
        location_mvpMatrix = super.getUniformLocation("mvpMatrix");
    }

    public void loadMVPMatrix(Matrix4f mvp) {
        super.loadMatrix(location_mvpMatrix, mvp);
    }
}
