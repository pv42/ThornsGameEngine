package engine.graphics.shaders;

import engine.graphics.cameras.Camera;
import engine.graphics.entities.Light;
import engine.toolbox.Settings;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;
import engine.toolbox.Maths;

import java.util.List;

/***
 * Created by pv42 on 17.06.16.
 */
public class StaticShader extends ShaderProgram{
    private static final String VERTEX_FILE = "src/engine/graphics/shaders/vertexShader";
    private static final String FRAGMENT_FILE = "src/engine/graphics/shaders/fragmentShader";
    private static final int MAX_LIGHTS = 4;
    private int location_transformationMatrix;
    private int location_projectionMatrix;
    private int location_viewMatrix;
    private int locations_lightPosition[];
    private int locations_lightColor[];
    private int locations_attenuation[];
    private int location_shineDamper;
    private int location_reflectivity;
    private int location_useFakeLightning;
    private int location_skyColor;
    private int location_numberOfRows;
    private int location_offset;
    private int location_specMap;
    private int location_usesSpecMap;
    private int location_texture;
    private int location_ambientLight;
    private int locations_bone[];
    private int location_useAnimation;
    public StaticShader() {
        super(VERTEX_FILE,FRAGMENT_FILE);
        start();
        loadAmbientLight(Settings.AMBIENT_LIGHT);
        stop();
    }
    @Override
    protected void bindAttributes() {
        super.bindAttribute(0, "position");
        super.bindAttribute(1, "uv");
        super.bindAttribute(2, "normal");
        super.bindAttribute(3, "bone_index"); //only used in animation
        super.bindAttribute(4, "bone_weight"); //same
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
        location_useFakeLightning = super.getUniformLocation("useFakeLightning");
        location_skyColor = super.getUniformLocation("skyColor");
        location_numberOfRows = super.getUniformLocation("numberOfRows");
        location_offset = super.getUniformLocation("offset");
        location_usesSpecMap = super.getUniformLocation("usesSpecularMap");
        location_specMap = super.getUniformLocation("specularMap");
        location_texture = super.getUniformLocation("diffTexture");
        location_ambientLight = super.getUniformLocation("ambient_light");
        locations_bone = new int[Settings.MAX_BONES];
        for (int i = 0; i < Settings.MAX_BONES; i++){
            locations_bone[i] = super.getUniformLocation("bone[" + i + "]");
        }
        location_useAnimation = super.getUniformLocation("useAnimation");

    }
    public void connectTextures() {
        super.loadInt(location_texture,0);
        super.loadInt(location_specMap,1);
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
    public void loadLights(List<Light> lights) {
        for (int i =0; i< MAX_LIGHTS; i++) {
            if (i < lights.size()) {
                super.loadVector(locations_lightPosition[i], lights.get(i).getPosition());
                super.loadVector(locations_lightColor[i], lights.get(i).getColor().getVector());
                super.loadVector(locations_attenuation[i], lights.get(i).getAttenuation());
            } else  {
                super.loadVector(locations_lightPosition[i], new Vector3f(0,0,0));
                super.loadVector(locations_lightColor[i], new Vector3f(0,0,0));
                super.loadVector(locations_attenuation[i], new Vector3f(1,0,0));
            }
        }
    }
    public void loadShineVariables(float shineDamper, float reflectivity) {
        super.loadFloat(location_shineDamper,shineDamper);
        super.loadFloat(location_reflectivity,reflectivity);
    }
    public void loadFakeLightning(boolean useFakeLightning) {
        super.loadBoolean(location_useFakeLightning, useFakeLightning);
    }
    public void loadUseSpecMap(boolean usesSpecMap) {
        super.loadBoolean(location_usesSpecMap, usesSpecMap);
    }
    public void loadSkyColor(float r, float g, float b) {
        super.loadVector(location_skyColor, new Vector3f(r,g,b));
    }
    public void loadNumberOfRows(int numberOfRows) {
        super.loadFloat(location_numberOfRows,numberOfRows);
    }
    public void loadOffset(float offsetX, float offsetY) {
        super.loadVector(location_offset,new Vector2f(offsetX,offsetY));
    }
    public void loadAmbientLight(float ambient) {
        super.loadFloat(location_ambientLight,ambient);
    }
    public void loadBones (List<Matrix4f> bones) {
        for(int i = 0; i < Settings.MAX_BONES; i++) {
            if (i < bones.size()) {
                super.loadMatrix(locations_bone[i], bones.get(i));
            } else  {
                super.loadMatrix(locations_bone[i], new Matrix4f());
            }
        }
    }
    public void loadUseAnimation(boolean useAnimation) {
        super.loadBoolean(location_useAnimation,true);
    }
}
