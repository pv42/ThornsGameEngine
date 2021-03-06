package engine.graphics.glglfwImplementation.shaders;

import org.joml.Matrix4f;
import org.joml.Vector2f;

import java.util.List;

import static engine.graphics.glglfwImplementation.GLLoader.*;
import static engine.toolbox.Settings.MAX_BONES;

/**
 * shader for entities, supports:
 * -texture atlases
 * -bone animation
 * -spec maps
 * -lighting
 * -animation (even tho it does not word)
 *
 * @author pv42
 */
public class EntityShader extends Lighted3DShader {
    private static final String VERTEX_FILE = "vertexShader";
    private static final String FRAGMENT_FILE = "fragmentShader";
    private int location_numberOfRows;
    private int location_offset;
    private int location_specMap;
    private int location_usesSpecMap;
    private int location_texture;
    private int location_bones;
    private int location_useAnimation;

    /**
     * creates a entity shader by loading and compiling the glsl files
     */
    public EntityShader() {
        super(VERTEX_FILE, FRAGMENT_FILE);
    }

    @Override
    protected void bindAttributes() {
        super.bindAttribute(VERTEX_ATTRIB_ARRAY_POSITION, "position");
        super.bindAttribute(VERTEX_ATTRIB_ARRAY_UV, "uv");
        super.bindAttribute(VERTEX_ATTRIB_ARRAY_NORMAL, "normal");
        super.bindAttribute(VERTEX_ATTRIB_ARRAY_BONE_INDICES, "bone_index"); //only used in animation
        super.bindAttribute(VERTEX_ATTRIB_ARRAY_BONE_WEIGHT, "bone_weight"); //same
    }

    @Override
    protected void getAllUniformLocations() {
        super.getAllUniformLocations();
        location_numberOfRows = super.getUniformLocation("numberOfRows");
        location_offset = super.getUniformLocation("offset");
        location_usesSpecMap = super.getUniformLocation("usesSpecularMap");
        location_specMap = super.getUniformLocation("specularMap");
        location_texture = super.getUniformLocation("diffTexture");
        location_bones = super.getUniformLocation("bone");
        location_useAnimation = super.getUniformLocation("useAnimation");
    }

    /**
     * connect the textures to the texture banks
     */
    public void connectTextures() {
        super.loadInt(location_texture, 0);
        super.loadInt(location_specMap, 1);
    }

    /**
     * loads if a specular map should be used
     *
     * @param usesSpecMap should a spec. map be used
     */
    public void loadUseSpecMap(boolean usesSpecMap) {
        super.loadBoolean(location_usesSpecMap, usesSpecMap);
    }

    /**
     * loads the number of rows if the texture is an atlas or 1
     *
     * @param numberOfRows the number of rows/cols the texture has
     */
    public void loadTextureAtlasNumberOfRows(int numberOfRows) {
        super.loadInt(location_numberOfRows, numberOfRows);
    }

    /**
     * loads the texture offset in texture atlases
     *
     * @param offsetX textures x offset
     * @param offsetY textures y offset
     */
    public void loadOffset(float offsetX, float offsetY) {
        super.loadVector(location_offset, new Vector2f(offsetX, offsetY));
    }

    /**
     * load the bone matrices into the shader uniforms
     *
     * @param bones bone matrices to load
     */
    public void loadBones(List<Matrix4f> bones) {
        super.loadMatrixArray(location_bones, bones, MAX_BONES);
    }

    /**
     * loads whether the rendered gpu should use bone animation
     *
     * @param useAnimation should animations be used
     */
    public void loadUseAnimation(boolean useAnimation) {
        super.loadBoolean(location_useAnimation, useAnimation);
    }
}
