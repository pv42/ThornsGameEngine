package engine.graphics.shaders;

import engine.graphics.cameras.Camera;
import engine.graphics.lights.Light;
import engine.toolbox.Color;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import engine.toolbox.Maths;

import java.util.List;

/**
  Created by pv42 on 18.06.16.
 */
public class TerrainShader extends Lighted3DShader{
    private static final String VERTEX_FILE = "src/engine/graphics/shaders/glsl/terrainVertexShader";
    private static final String FRAGMENT_FILE = "src/engine/graphics/shaders/glsl/terrainFragmentShader";
    private static final int MAX_LIGHTS = 4;
    private int location_bgTexture,
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
        super.getAllUniformLocations();
        location_bgTexture = super.getUniformLocation("bgTexture");
        location_rTexture = super.getUniformLocation("rTexture");
        location_gTexture = super.getUniformLocation("gTexture");
        location_bTexture = super.getUniformLocation("bTexture");
        location_blendMap = super.getUniformLocation("blendMap");
    }

    public void connectTextures() {
        super.loadInt(location_bgTexture,0);
        super.loadInt(location_rTexture,1);
        super.loadInt(location_gTexture,2);
        super.loadInt(location_bTexture,3);
        super.loadInt(location_blendMap,4);
    }
}
