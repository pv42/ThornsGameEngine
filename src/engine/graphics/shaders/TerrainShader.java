package engine.graphics.shaders;

import engine.graphics.cameras.Camera;
import engine.graphics.entities.Light;
import engine.toolbox.Color;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import engine.toolbox.Maths;

import java.util.List;

/**
  Created by pv42 on 18.06.16.
 */
public class TerrainShader extends ShaderProgram{
    private static final String VERTEX_FILE = "src/engine/graphics/shaders/terrainVertexShader";
    private static final String FRAGMENT_FILE = "src/engine/graphics/shaders/terrainFragmentShader";
    private static final int MAX_LIGHTS = 4;
    private int location_transformationMatrix,
            location_projectionMatrix,
            location_viewMatrix,
            locations_lightPosition[],
            locations_lightColor[],
            locations_attenuation[],
            location_shineDamper,
            location_reflectivity,
            location_skyColor,
            location_bgTexture,
            location_rTexture,
            location_gTexture,
            location_bTexture,
            location_blendMap;
    public TerrainShader() {
        super(VERTEX_FILE,FRAGMENT_FILE);
    }
    @Override
    protected void bindAttributes() {
        super.bindAttribute(0, "position");
        super.bindAttribute(1, "uv");
        super.bindAttribute(2, "normal");
    }

    @Override
    protected void getAllUniformLocations() {
        location_transformationMatrix = super.getUniformLocation("transformationMatrix");
        location_projectionMatrix = super.getUniformLocation("projectionMatrix");
        location_viewMatrix = super.getUniformLocation("viewMatrix");
        locations_lightPosition = new int[MAX_LIGHTS];
        locations_lightColor = new int[MAX_LIGHTS];
        locations_attenuation = new int[MAX_LIGHTS];
        for (int i = 0; i<4;i++) {
            locations_lightPosition[i] = super.getUniformLocation("lightPosition[" + i + "]" );
            locations_lightColor[i] = super.getUniformLocation("lightColor[" + i + "]");
            locations_attenuation[i] = super.getUniformLocation("attenuation[" + i + "]");
        }
        location_shineDamper = super.getUniformLocation("shineDamper");
        location_reflectivity = super.getUniformLocation("reflectivity");
        location_skyColor = super.getUniformLocation("skyColor");
        location_bgTexture = super.getUniformLocation("bgTexture");
        location_rTexture = super.getUniformLocation("rTexture");
        location_gTexture = super.getUniformLocation("gTexture");
        location_bTexture = super.getUniformLocation("bTexture");
        location_blendMap = super.getUniformLocation("blendMap");
    }
    public void loadTransformationMatrix(Matrix4f matrix) {
        super.loadMatrix(location_transformationMatrix,matrix);
    }
    public void loadProjectionMatrix(Matrix4f matrix) {
        super.loadMatrix(location_projectionMatrix,matrix);
    }
    public void loadViewMatrix(Camera camera) {
        Matrix4f viewMatrix = Maths.createViewMatrix(camera);
        super.loadMatrix(location_viewMatrix,viewMatrix);
    }
    public void loadLight(List<Light> lights) {
        for (int i = 0; i < MAX_LIGHTS; i++) {
            if (i < lights.size()) {
                super.loadVector(locations_lightPosition[i], lights.get(i).getPosition());
                super.loadVector(locations_lightColor[i], lights.get(i).getColor().getVector());
                super.loadVector(locations_attenuation[i], lights.get(i).getAttenuation());
            } else {
                super.loadVector(locations_lightPosition[i], new Vector3f(0, 0, 0));
                super.loadVector(locations_lightColor[i], new Vector3f(0, 0, 0));
                super.loadVector(locations_attenuation[i], new Vector3f(1, 0, 0));
            }
        }
    }
    public void loadShineVariables(float shineDamper, float reflectivity) {
        super.loadFloat(location_shineDamper,shineDamper);
        super.loadFloat(location_reflectivity,reflectivity);
    }
    public void loadSkyColor(Color skyColor) {
        super.loadVector(location_skyColor, skyColor.getVector());
    }
    public void connectTextures() {
        super.loadInt(location_bgTexture,0);
        super.loadInt(location_rTexture,1);
        super.loadInt(location_gTexture,2);
        super.loadInt(location_bTexture,3);
        super.loadInt(location_blendMap,4);
    }
}
