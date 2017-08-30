package engine.graphics.skybox;

import engine.graphics.cameras.Camera;
import engine.toolbox.Color;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import engine.graphics.display.DisplayManager;
import engine.graphics.shaders.ShaderProgram;
import engine.toolbox.Maths;

/**
   Created by pv42 on 21.06.16.
 */
public class SkyBoxShader extends ShaderProgram {
    private static final String FRAGMENT_FILE = "src/engine/graphics/skybox/skyboxFragment.glsl";
    private static final String VERTEX_FILE = "src/engine/graphics/skybox/skyboxVertex.glsl";
    private final float ROTATE_SPEED = 0.5f;
    private int location_viewMatrix;
    private int location_projectionMatrix;
    private int location_fogColor;
    private int location_cubeMap;
    private float rotation = 0;
    public SkyBoxShader() {
        super(VERTEX_FILE, FRAGMENT_FILE);
    }

    @Override
    protected void bindAttributes() {
        super.bindAttribute(0, "position");
    }

    @Override
    protected void getAllUniformLocations() {
        location_projectionMatrix = getUniformLocation("projectionMatrix");
        location_viewMatrix = getUniformLocation("viewMatrix");
        location_fogColor = getUniformLocation("fogColor");
        location_cubeMap = getUniformLocation("cubeMap");
    }
    protected void loadProjectionMatrix(Matrix4f projectionMatrix) {
        super.loadMatrix(location_projectionMatrix,projectionMatrix);
    }
    protected void loadViewMatrix(Camera camera) {
        Matrix4f matrix = Maths.createViewMatrix(camera);
        matrix.m30(0);
        matrix.m31(0);
        matrix.m32(0);
        rotation += ROTATE_SPEED * DisplayManager.getFrameTimeSeconds();
        matrix.rotate((float) Math.toRadians(rotation), new Vector3f(0,1,0));
        super.loadMatrix(location_viewMatrix, matrix);
    }
    public void loadFogColor(Color fogColor){
        super.loadVector(location_fogColor,fogColor.getVector());
    }
    public void connectTextures() {
        super.loadInt(location_cubeMap,0);
    }
}
