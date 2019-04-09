package engine.graphics.glglfwImplementation.shaders;

import engine.graphics.lights.PointLight;
import engine.toolbox.Color;
import org.joml.Matrix4f;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;

/**
 * a lighted 3 dimensional glsl shader program
 * @author pv42
 */
public abstract class Lighted3DShader extends ShaderProgram {
    private static final int MAX_LIGHTS = 4;
    private static final String TAG = "Lighted3DShader";
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
    }

    @Override
    protected void getAllUniformLocations() {
        location_transformationMatrix = super.getUniformLocation("transformationMatrix");
        location_projectionMatrix = super.getUniformLocation("projectionMatrix");
        location_viewMatrix = super.getUniformLocation("viewMatrix");
        locations_lightPosition = super.getUniformLocation("lightPosition");
        locations_lightColor = super.getUniformLocation("lightColor");
        locations_attenuation = super.getUniformLocation("lightAttenuation");
        location_shineDamper = super.getUniformLocation("shineDamper");
        location_reflectivity = super.getUniformLocation("reflectivity");
        location_useFakeLightning = super.getUniformLocation("useFakeLightning");
        location_skyColor = super.getUniformLocation("skyColor");
        location_ambientLight = super.getUniformLocation("ambient_light");
    }

    /**
     * load transformation matrix
     *
     * @param matrix transformation matrix to use
     */
    public void loadTransformationMatrix(Matrix4f matrix) {
        super.loadMatrix(location_transformationMatrix, matrix);
    }

    /**
     * loads projection matrix
     *
     * @param matrix projection matrix
     */
    public void loadProjectionMatrix(Matrix4f matrix) {
        super.loadMatrix(location_projectionMatrix, matrix);
    }

    /**
     * loads the view matrix usually produced by the camera
     *
     * @param viewMatrix view matrix to use
     */
    public void loadViewMatrix(Matrix4f viewMatrix) {
        super.loadMatrix(location_viewMatrix, viewMatrix);
    }

    /**
     * loads light sources with position, color and attenuation
     *
     * @param lights light sources
     */
    public void loadLights(List<PointLight> lights) {
        List<Vector3f> lightPositions = new ArrayList<>();
        List<Vector3f> lightColors = new ArrayList<>();
        List<Vector3f> attenuations = new ArrayList<>();
        for (PointLight light : lights) {
            lightPositions.add(light.getPosition());
            lightColors.add(light.getColor().getVector());
            attenuations.add(light.getAttenuation());
        }
        super.loadVectorArray(locations_lightPosition, lightPositions, MAX_LIGHTS);
        super.loadVectorArray(locations_lightColor, lightColors, MAX_LIGHTS);
        super.loadVectorArray(locations_attenuation, attenuations, MAX_LIGHTS);
    }

    /**
     * loads materials shineDamper and reflectivity
     * @param shineDamper shine damper value
     * @param reflectivity reflectivity to use
     */
    public void loadShineVariables(float shineDamper, float reflectivity) {
        super.loadFloat(location_shineDamper, shineDamper);
        super.loadFloat(location_reflectivity, reflectivity);
    }

    /**
     * loads if fake lightning should be used fake lightning overwrites the normals light calculation and
     * always produces maximum brightness
     * @param useFakeLightning use fake lightning ?
     */
    public void loadFakeLightning(boolean useFakeLightning) {
        super.loadBoolean(location_useFakeLightning, useFakeLightning);
    }

    /**
     * loads the sky color to use for distance fog into sky blending
     * @param skyColor sky color to use for the fog blending
     */
    public void loadSkyColor(Color skyColor) {
        super.loadVector(location_skyColor, skyColor.getVector());
    }

    /**
     * loads the amount of ambient (minimal) light to use as a float from 0 to 1 with 0 equals no ambient light
     * and 1 maximal brightness
     * @param ambient amount of ambient light
     */
    public void loadAmbientLight(float ambient) {
        super.loadFloat(location_ambientLight, ambient);
    }
}
