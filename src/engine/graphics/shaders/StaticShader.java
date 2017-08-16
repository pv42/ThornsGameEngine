package engine.graphics.shaders;

import engine.graphics.cameras.Camera;
import engine.graphics.lights.Light;
import engine.toolbox.Color;
import engine.toolbox.Settings;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;
import engine.toolbox.Maths;

import java.util.List;

/***
 * Created by pv42 on 17.06.16.
 */
public class StaticShader extends Lighted3DShader {
    private static final String VERTEX_FILE = "src/engine/graphics/shaders/glsl/vertexShader";
    private static final String FRAGMENT_FILE = "src/engine/graphics/shaders/glsl/fragmentShader";
    private int location_numberOfRows;
    private int location_offset;
    private int location_specMap;
    private int location_usesSpecMap;
    private int location_texture;
    private int locations_bone; //todo
    private int location_useAnimation;

    public StaticShader() {
        super(VERTEX_FILE, FRAGMENT_FILE);
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
        super.getAllUniformLocations();
        location_numberOfRows = super.getUniformLocation("numberOfRows");
        location_offset = super.getUniformLocation("offset");
        location_usesSpecMap = super.getUniformLocation("usesSpecularMap");
        location_specMap = super.getUniformLocation("specularMap");
        location_texture = super.getUniformLocation("diffTexture");
        locations_bone = super.getUniformLocation("bone");
        /*for (int i = 0; i < Settings.MAX_BONES; i++) {
            locations_bone[i] = super.getUniformLocation("bone[" + i + "]");
        }*/
        location_useAnimation = super.getUniformLocation("useAnimation");
    }

    public void connectTextures() {
        super.loadInt(location_texture, 0);
        super.loadInt(location_specMap, 1);
    }

    public void loadUseSpecMap(boolean usesSpecMap) {
        super.loadBoolean(location_usesSpecMap, usesSpecMap);
    }

    public void loadNumberOfRows(int numberOfRows) {
        super.loadFloat(location_numberOfRows, numberOfRows);
    }

    public void loadOffset(float offsetX, float offsetY) {
        super.loadVector(location_offset, new Vector2f(offsetX, offsetY));
    }
    //todo
    @Deprecated
    public void loadBones(List<Matrix4f> bones) {
        /*for (int i = 0; i < Settings.MAX_BONES; i++) {
            if (i < bones.size()) {
                super.loadMatrix(locations_bone[i], bones.get(i));
            } else {
                super.loadMatrix(locations_bone[i], new Matrix4f());
            }
        }*/
    }

    public void loadUseAnimation(boolean useAnimation) {
        super.loadBoolean(location_useAnimation, true);
    }
}
