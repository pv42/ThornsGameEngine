package engine.graphics.shaders;

import engine.graphics.cameras.Camera;
import engine.graphics.lights.Light;
import engine.toolbox.Color;
import engine.toolbox.Maths;
import engine.toolbox.Settings;
import org.joml.Matrix4f;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;

public abstract class Lighted3DShader extends ShaderProgram {
    private static final int MAX_LIGHTS = 4;
    private int location_transformationMatrix;
    private int location_projectionMatrix;
    private int location_viewMatrix;
    private int locations_lightPosition;
    private int locations_lightColor;
    private int locations_attenuation;
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
        start();
        loadAmbientLight(Settings.AMBIENT_LIGHT);
        stop();
    }

    @Override
    protected void getAllUniformLocations() {
        location_transformationMatrix = super.getUniformLocation("transformationMatrix");
        location_projectionMatrix = super.getUniformLocation("projectionMatrix");
        location_viewMatrix = super.getUniformLocation("viewMatrix");
        locations_lightPosition = super.getUniformLocation("lightPosition");
        locations_lightColor = super.getUniformLocation("lightColor");
        locations_attenuation = super.getUniformLocation("attenuation");

        /*super.getUniformLocationsArray("lightPosition", locations_lightPosition, MAX_LIGHTS);
        super.getUniformLocationsArray("lightColor", locations_lightColor, MAX_LIGHTS);
        super.getUniformLocationsArray("attenuation", locations_attenuation, MAX_LIGHTS);
        */
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
        List<Vector3f> lightPositions = new ArrayList<>();
        List<Vector3f> lightColors = new ArrayList<>();
        List<Vector3f> attenuations = new ArrayList<>();
        for (Light light: lights) {
            lightPositions.add(light.getPosition());
            lightColors.add(light.getColor().getVector());
            attenuations.add(light.getAttenuation());
            System.out.println(light.getPosition());
        }
        super.loadVectorArray(locations_lightPosition, lightPositions, MAX_LIGHTS);
        super.loadVectorArray(locations_lightColor, lightColors, MAX_LIGHTS);
        super.loadVectorArray(locations_attenuation, attenuations, MAX_LIGHTS);
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
