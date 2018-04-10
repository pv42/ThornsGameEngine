package engine.graphics.shaders;

import engineTester.Matrix4fDbg;
import org.joml.Matrix4f;
import org.joml.Vector2f;

import java.util.List;

import static engine.toolbox.Settings.MAX_BONES;

/***
 * Created by pv42 on 17.06.16.
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
     * shader for entities, supports:
     * -texture atlases
     * -bone animation
     * -spec maps
     * -lighting
     * - ...
     */
    public EntityShader() {
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
     * @param usesSpecMap should a spec. map be used
     */
    public void loadUseSpecMap(boolean usesSpecMap) {
        super.loadBoolean(location_usesSpecMap, usesSpecMap);
    }

    /**
     * loads the number of rows if the texture is an atlas or 1
     * @param numberOfRows the number of rows/cols the texture has
     */
    public void loadNumberOfRows(int numberOfRows) {
        super.loadFloat(location_numberOfRows, numberOfRows);
    }

    /**
     * loads the texture offset in texture atlases
     * @param offsetX textures x offset
     * @param offsetY textures y offset
     */
    public void loadOffset(float offsetX, float offsetY) {
        super.loadVector(location_offset, new Vector2f(offsetX, offsetY));
    }

    /**
     * load the bone matrices into the shader uniforms
     * @param bones bone matrices to load
     */
    public void loadBones(List<Matrix4fDbg> bones) {
         /*for(Matrix4fDbg mat: bones) {
             System.out.println(mat.getName());
         }
         System.out.println("END\n");*/
         super.loadMatrixArray(location_bones, bones, MAX_BONES);
    }

    /**
     * loads whether the rendered gpu should use bone animation
     * @param useAnimation should animations be used
     */
    public void loadUseAnimation(boolean useAnimation) {
        super.loadBoolean(location_useAnimation, useAnimation);
    }
}
