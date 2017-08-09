package engine.graphics.shaders;

import engine.graphics.cameras.Camera;
import engine.graphics.lights.Light;
import engine.toolbox.Color;
import engine.toolbox.Maths;
import org.joml.Matrix4f;
import org.joml.Vector3f;

import java.util.List;

public abstract class Lighted3DShader extends ShaderProgram {
    private static final int MAX_LIGHTS = 4;
    private int location_transformationMatrix;
    private int location_projectionMatrix;
    private int location_viewMatrix;
    private int[] locations_lightPosition;
    private int[] locations_lightColor;
    private int[] locations_attenuation;
    private int location_shineDamper;
    private int location_reflectivity;
    private int location_useFakeLightning;
    private int location_skyColor;
    private int location_ambientLight;

    /**
     * creates a shader program from vertex and fragment file
     *
     * @param vertexFile   vertex file location
     * @param fragmentFile fragment file location
     */
    public Lighted3DShader(String vertexFile, String fragmentFile) {
        super(vertexFile, fragmentFile);
    }

    @Override
    protected void getAllUniformLocations() {
        location_transformationMatrix = super.getUniformLocation("transformationMatrix");
        location_projectionMatrix = super.getUniformLocation("projectionMatrix");
        location_viewMatrix = super.getUniformLocation("viewMatrix");
        locations_lightPosition = new int[MAX_LIGHTS];
        locations_lightColor = new int[MAX_LIGHTS];
        locations_attenuation = new int[MAX_LIGHTS];
        super.getUniformLocationsArray("lightPosition", locations_lightPosition, MAX_LIGHTS);
        super.getUniformLocationsArray("lightColor", locations_lightColor, MAX_LIGHTS);
        super.getUniformLocationsArray("attenuation", locations_attenuation, MAX_LIGHTS);
        /*for (int i = 0; i < 4; i++) {
            locations_lightPosition[i] = super.getUniformLocation("lightPosition[" + i + "]");
            locations_lightColor[i] = super.getUniformLocation("lightColor[" + i + "]");
            locations_attenuation[i] = super.getUniformLocation("attenuation[" + i + "]");
        }*///todo remove if getLoc...Arr.. works
        location_shineDamper = super.getUniformLocation("shineDamper");
        location_reflectivity = super.getUniformLocation("reflectivity");
        location_useFakeLightning = super.getUniformLocation("useFakeLightning");
        location_skyColor = super.getUniformLocation("skyColor");
        location_ambientLight = super.getUniformLocation("ambient_light");
    }

    public void loadTransformationMatrix(Matrix4f matrix) {
        super.loadMatrix(location_transformationMatrix, matrix);
    }

    public void loadProjectionMatrix(Matrix4f matrix) {
        super.loadMatrix(location_projectionMatrix, matrix);
    }

    public void loadViewMatrix(Camera camera) {
        Matrix4f viewMatrix = Maths.createViewMatrix(camera);
        super.loadMatrix(location_viewMatrix, viewMatrix);
    }

    public void loadLights(List<Light> lights) {
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
        super.loadFloat(location_shineDamper, shineDamper);
        super.loadFloat(location_reflectivity, reflectivity);
    }

    public void loadFakeLightning(boolean useFakeLightning) {
        super.loadBoolean(location_useFakeLightning, useFakeLightning);
    }

    public void loadSkyColor(Color skyColor) {
        super.loadVector(location_skyColor, skyColor.getVector());
    }

    public void loadAmbientLight(float ambient) {
        super.loadFloat(location_ambientLight, ambient);
    }

}
