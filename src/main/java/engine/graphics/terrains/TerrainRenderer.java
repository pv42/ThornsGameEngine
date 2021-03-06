package engine.graphics.terrains;

import java.util.List;

import engine.graphics.cameras.Camera;
import engine.graphics.glglfwImplementation.models.GLRawModel;
import engine.graphics.lights.PointLight;

import org.lwjgl.opengl.*;
import org.joml.Matrix4f;
import org.joml.Vector3f;

import engine.graphics.glglfwImplementation.textures.TerrainTexturePack;
import engine.toolbox.Maths;

import static engine.graphics.glglfwImplementation.GLLoader.VERTEX_ATTRIB_ARRAY_NORMAL;
import static engine.graphics.glglfwImplementation.GLLoader.VERTEX_ATTRIB_ARRAY_POSITION;
import static engine.graphics.glglfwImplementation.GLLoader.VERTEX_ATTRIB_ARRAY_UV;
import static engine.toolbox.Settings.SKY_COLOR;


public class TerrainRenderer {

    private TerrainShader shader;

    public TerrainRenderer(TerrainShader shader, Matrix4f projectionMatrix) {
        this.shader = shader;
        shader.start();
        shader.loadProjectionMatrix(projectionMatrix);
        shader.connectTextures();
        shader.stop();
    }

    public void updateProjectionMatrix(Matrix4f projectionMatrix) {
        shader.start();
        shader.loadProjectionMatrix(projectionMatrix);
        shader.stop();
    }

    public void render(List<Terrain> terrains, Camera camera, List<PointLight> lights, Matrix4f shadowSpaceMatrix, int shadowMapTexture) {
        shader.start();
        shader.loadShadowSpaceMatrix(shadowSpaceMatrix);
        shader.loadViewMatrix(camera.getViewMatrix());
        shader.loadLights(lights);
        shader.loadSkyColor(SKY_COLOR);
        GL11.glDisable(GL11.GL_BLEND);
        for (Terrain terrain : terrains) {
            prepareTerrain(terrain, shadowMapTexture);
            loadModelMatrix(terrain);
            GL11.glDrawElements(GL11.GL_TRIANGLES, terrain.getModel().getVertexCount(),
                    GL11.GL_UNSIGNED_INT, 0);
            unbindTexturedModel();
        }
        shader.stop();
    }

    private void prepareTerrain(Terrain terrain, int shadowMapTexture) {
        GLRawModel rawModel = terrain.getModel();
        GL30.glBindVertexArray(rawModel.getVaoID());
        GL20.glEnableVertexAttribArray(0);
        GL20.glEnableVertexAttribArray(1);
        GL20.glEnableVertexAttribArray(2);
        bindTextures(terrain, shadowMapTexture);
        shader.loadShineVariables(1,0);
    }

    private void bindTextures(Terrain terrain, int shadowMapTexture) {
        TerrainTexturePack texturePack = terrain.getTexturePack();
        GL13.glActiveTexture(GL13.GL_TEXTURE0);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, texturePack.getBgTexture().getID());
        GL13.glActiveTexture(GL13.GL_TEXTURE1);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, texturePack.getrTexture().getID());
        GL13.glActiveTexture(GL13.GL_TEXTURE2);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, texturePack.getgTexture().getID());
        GL13.glActiveTexture(GL13.GL_TEXTURE3);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, texturePack.getbTexture().getID());
        GL13.glActiveTexture(GL13.GL_TEXTURE4);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, terrain.getBlendMap().getID());
        GL13.glActiveTexture(GL13.GL_TEXTURE5);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, shadowMapTexture);

    }

    private void unbindTexturedModel() {
        GL20.glDisableVertexAttribArray(VERTEX_ATTRIB_ARRAY_POSITION);
        GL20.glDisableVertexAttribArray(VERTEX_ATTRIB_ARRAY_UV);
        GL20.glDisableVertexAttribArray(VERTEX_ATTRIB_ARRAY_NORMAL);
        GL30.glBindVertexArray(0);
    }

    private void loadModelMatrix(Terrain terrain) {
        Matrix4f transformationMatrix = Maths.createTransformationMatrix(
                new Vector3f(terrain.getX(), 0, terrain.getZ()), 0, 0, 0, 1);
        shader.loadTransformationMatrix(transformationMatrix);
    }

}
