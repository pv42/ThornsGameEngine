package engine.graphics.terrains;


import engine.graphics.glglfwImplementation.shaders.Lighted3DShader;
import org.joml.Matrix4f;

/**
 * Created by pv42 on 18.06.16.
 */
public class TerrainShader extends Lighted3DShader {
    private static final String VERTEX_FILE = "terrainVertexShader";
    private static final String FRAGMENT_FILE = "terrainFragmentShader";
    private int location_bgTexture;
    private int location_rTexture;
    private int location_gTexture;
    private int location_bTexture;
    private int location_blendMap;
    private int location_shadowSpaceMatrix;
    private int location_shadowMap;

    public TerrainShader() {
        super(VERTEX_FILE, FRAGMENT_FILE);
    }

    @Override
    protected void bindAttributes() {
        super.bindAttribute(0, "position");
        super.bindAttribute(1, "uv");
        super.bindAttribute(2, "normal");
    }

    @Override
    protected void getAllUniformLocations() {
        super.getAllUniformLocations();
        location_bgTexture = super.getUniformLocation("bgTexture");
        location_rTexture = super.getUniformLocation("rTexture");
        location_gTexture = super.getUniformLocation("gTexture");
        location_bTexture = super.getUniformLocation("bTexture");
        location_blendMap = super.getUniformLocation("blendMap");
        location_shadowSpaceMatrix = super.getUniformLocation("shadowSpaceMatrix");
        location_shadowMap = super.getUniformLocation("shadowMap");
    }

    /**
     * connect texture uniform to texture bank
     */
    public void connectTextures() {
        super.loadInt(location_bgTexture, 0);
        super.loadInt(location_rTexture, 1);
        super.loadInt(location_gTexture, 2);
        super.loadInt(location_bTexture, 3);
        super.loadInt(location_blendMap, 4);
        super.loadInt(location_shadowMap, 5);
    }

    public void loadShadowSpaceMatrix(Matrix4f matrix) {
        super.loadMatrix(location_shadowSpaceMatrix, matrix);
    }
}
