package engine.graphics.lines;

import engine.graphics.cameras.Camera;
import engine.graphics.cameras.ThreeDimensionCamera;
import engine.graphics.shaders.ShaderProgram;
import engine.toolbox.Color;
import engine.toolbox.Maths;
import org.joml.Matrix4f;

/***
 * Created by pv42 on 12.08.16.
 */
public class LineShader extends ShaderProgram {

    private static final String VERTEX_FILE = "lineVertexShader";
    private static final String FRAGMENT_FILE = "lineFragmentShader";
    private int location_transformationMatrix,
            location_projectionMatrix,
            location_viewMatrix,
            location_color;
    public LineShader() {
        super(VERTEX_FILE,FRAGMENT_FILE);
    }
    @Override
    protected void bindAttributes() {
        super.bindAttribute(0, "position");
    }

    @Override
    protected void getAllUniformLocations() {
        location_transformationMatrix = super.getUniformLocation("transformationMatrix");
        location_projectionMatrix = super.getUniformLocation("projectionMatrix");
        location_viewMatrix = super.getUniformLocation("viewMatrix");
        location_color = super.getUniformLocation("color");
    }
    public void loadTransformationMatrix(Matrix4f matrix) {
        super.loadMatrix(location_transformationMatrix,matrix);
    }
    public void loadProjectionMatrix(Matrix4f matrix) {
        super.loadMatrix(location_projectionMatrix,matrix);
    }
    public void loadViewMatrix(Camera camera) {
        Matrix4f viewMatrix = camera.getViewMatrix();
        super.loadMatrix(location_viewMatrix,viewMatrix);
    }
    public void loadColor(Color color) {
        super.loadVector(location_color,color.getVector());
    }

}
