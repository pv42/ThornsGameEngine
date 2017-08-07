package engine.graphics.animation;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import engine.graphics.entities.Entity;
import engine.graphics.models.RawModel;
import engine.graphics.models.TexturedModel;
import engine.graphics.shaders.StaticShader;
import engine.toolbox.Settings;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.joml.Matrix4f;
import engine.graphics.cameras.Camera;
import engine.graphics.entities.Light;
import engine.graphics.renderEngine.MasterRenderer;
import engine.graphics.textures.ModelTexture;
import engine.toolbox.Maths;


/***
 * Created by pv42 on 27.07.16.
 */
@Deprecated
public class AnimatedEntityRenderer {
    private StaticShader shader;

    public AnimatedEntityRenderer(Matrix4f projectionMatrix) {
        shader = new StaticShader();
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

    public void render(Map<List<TexturedModel>, List<Entity>> entities, List<Light> lights, Camera camera) {
        prepare(lights, camera);
        for (List<TexturedModel> models : entities.keySet()) {
            for (TexturedModel model : models) {
                prepareTexturedModel(model);
                List<Entity> batch = entities.get(models);
                for (Entity entity : batch) {
                    prepareInstance(entity);
                    GL11.glDrawElements(GL11.GL_TRIANGLES, model.getRawModel().getVertexCount(),
                            GL11.GL_UNSIGNED_INT, 0);
                }
                unbindTexturedModel();
            }
        }
        shader.stop();
    }

    public void cleanUp() {
        shader.cleanUp();
    }

    private void prepareTexturedModel(TexturedModel model) {
        RawModel rawModel = model.getRawModel();
        List<Bone> bones = rawModel.getBones();
        List<Matrix4f> boneMatrices = bones.stream().map(Bone::getTransformationMatrix).collect(Collectors.toList());
        GL30.glBindVertexArray(rawModel.getVaoID());
        GL20.glEnableVertexAttribArray(0);
        GL20.glEnableVertexAttribArray(1);
        GL20.glEnableVertexAttribArray(2);
        GL20.glEnableVertexAttribArray(3);
        GL20.glEnableVertexAttribArray(4);
        ModelTexture texture = model.getTexture();
        shader.loadUseAnimation(true);
        shader.loadNumberOfRows(texture.getNumberOfRows());
        if (texture.isHasTransparency()) {
            MasterRenderer.disableCulling();
        }
        shader.loadFakeLightning(texture.isUseFakeLightning());
        shader.loadShineVariables(texture.getShineDamper(), texture.getReflectivity());
        shader.loadBones(boneMatrices);
        GL13.glActiveTexture(GL13.GL_TEXTURE0);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, model.getTexture().getID());
        shader.loadUseSpecMap(texture.hasSpecularMap());
        if (texture.hasSpecularMap()) {
            GL13.glActiveTexture(GL13.GL_TEXTURE1);
            GL11.glBindTexture(GL11.GL_TEXTURE_2D, model.getTexture().getSpecularMapID());
        }
    }

    private void unbindTexturedModel() {
        MasterRenderer.enableCulling();
        GL20.glDisableVertexAttribArray(0);
        GL20.glDisableVertexAttribArray(1);
        GL20.glDisableVertexAttribArray(2);
        GL20.glDisableVertexAttribArray(3);
        GL20.glDisableVertexAttribArray(4);
        GL30.glBindVertexArray(0);
    }

    private void prepareInstance(Entity entity) {
        Matrix4f transformationMatrix = Maths.createTransformationMatrix(entity.getPosition(),
                entity.getRx(), entity.getRy(), entity.getRz(), entity.getScale());
        shader.loadTransformationMatrix(transformationMatrix);
        shader.loadOffset(entity.getTextureXOffset(), entity.getTextureYOffset());
    }

    private void prepare(List<Light> lights, Camera camera) {
        shader.start();
        shader.loadSkyColor(Settings.SKY_COLOR);
        shader.loadLights(lights);
        shader.loadViewMatrix(camera);
    }
}