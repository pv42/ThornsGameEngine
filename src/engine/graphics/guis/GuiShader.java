package engine.graphics.guis;

import org.joml.Matrix4f;
import engine.graphics.shaders.ShaderProgram;

/**
 * Created by pv42 on 20.06.16.
 */
public class GuiShader  extends ShaderProgram{
    private static final String VERTEX_FILE = "guiVertexShader";
    private static final String FRAGMENT_FILE = "guiFragmentShader";

    private int location_transformationMatrix;
    public GuiShader() {
        super(VERTEX_FILE,FRAGMENT_FILE);
    }
    public void loadTransformationMatrix(Matrix4f matrix) {
        super.loadMatrix(location_transformationMatrix, matrix);
    }
    @Override
    protected void getAllUniformLocations() {
        location_transformationMatrix = super.getUniformLocation("transformationMatrix");
    }
    @Override protected void bindAttributes() {
        super.bindAttribute(0, "position");
    }
}
