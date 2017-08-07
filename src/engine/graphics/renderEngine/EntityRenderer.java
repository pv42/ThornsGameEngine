package engine.graphics.renderEngine;

import engine.graphics.animation.Bone;
import engine.graphics.cameras.Camera;
import engine.graphics.entities.Entity;
import engine.graphics.entities.Light;
import engine.graphics.models.RawModel;
import engine.graphics.models.TexturedModel;
import org.lwjgl.opengl.*;
import org.joml.Matrix4f;
import engine.graphics.shaders.StaticShader;
import engine.graphics.textures.ModelTexture;
import engine.toolbox.Maths;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static engine.graphics.renderEngine.Loader.VERTEX_ATTRIB_ARRAY_POSITION;
import static engine.graphics.renderEngine.Loader.VERTEX_ATTRIB_ARRAY_UV;
import static engine.graphics.renderEngine.Loader.VERTEX_ATTRIB_ARRAY_NORMAL;
import static engine.graphics.renderEngine.Loader.VERTEX_ATTRIB_ARRAY_BONEINDICES;
import static engine.graphics.renderEngine.Loader.VERTEX_ATTRIB_ARRAY_BONEWEIGHT;
import static engine.toolbox.Settings.SKY_COLOR;


/***
 * Created by pv42 on 17.06.16.
 */
public class EntityRenderer {
    private StaticShader shader;

    public EntityRenderer(Matrix4f projectionMatrix) {
        this.shader = new StaticShader();
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

    private void prepareTexturedModel(TexturedModel model) {

        RawModel rawModel = model.getRawModel();
        List<Bone> bones;
        List<Matrix4f> boneMatrices = null;
        if (model.isAnimated()) {
            bones = rawModel.getBones();
            boneMatrices = bones.stream().map(Bone::getTransformationMatrix).collect(Collectors.toList());
        }
        GL30.glBindVertexArray(rawModel.getVaoID());
        GL20.glEnableVertexAttribArray(VERTEX_ATTRIB_ARRAY_POSITION);
        GL20.glEnableVertexAttribArray(VERTEX_ATTRIB_ARRAY_UV);
        GL20.glEnableVertexAttribArray(VERTEX_ATTRIB_ARRAY_NORMAL);
        if (model.isAnimated()) {
            GL20.glEnableVertexAttribArray(VERTEX_ATTRIB_ARRAY_BONEINDICES);
            GL20.glEnableVertexAttribArray(VERTEX_ATTRIB_ARRAY_BONEWEIGHT);
        }
        ModelTexture texture = model.getTexture();
        shader.loadUseAnimation(model.isAnimated());
        shader.loadNumberOfRows(texture.getNumberOfRows());
        if (texture.isHasTransparency()) {
            MasterRenderer.disableCulling();
        }
        shader.loadFakeLightning(texture.isUseFakeLightning());
        shader.loadShineVariables(texture.getShineDamper(), texture.getReflectivity());
        if (model.isAnimated()) shader.loadBones(boneMatrices);
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
        GL20.glDisableVertexAttribArray(VERTEX_ATTRIB_ARRAY_POSITION);
        GL20.glDisableVertexAttribArray(VERTEX_ATTRIB_ARRAY_UV);
        GL20.glDisableVertexAttribArray(VERTEX_ATTRIB_ARRAY_NORMAL);
        GL20.glDisableVertexAttribArray(VERTEX_ATTRIB_ARRAY_BONEINDICES); //ani
        GL20.glDisableVertexAttribArray(VERTEX_ATTRIB_ARRAY_BONEWEIGHT); //ani
        GL30.glBindVertexArray(0); //unbind vertex array

    }

    private void prepareInstance(Entity entity) {
        Matrix4f transformationMatrix = Maths.createTransformationMatrix(entity.getPosition(),
                entity.getRx(), entity.getRy(), entity.getRz(), entity.getScale());
        shader.loadTransformationMatrix(transformationMatrix);
        shader.loadOffset(entity.getTextureXOffset(), entity.getTextureYOffset());
    }

    private void prepare(List<Light> lights, Camera camera) {
        shader.start();
        shader.loadSkyColor(SKY_COLOR);
        shader.loadLights(lights);
        shader.loadViewMatrix(camera);
    }

    public void cleanUp() {
        shader.cleanUp();
    }

    @Deprecated
    public void render(RawModel model) {
        GL30.glBindVertexArray(model.getVaoID());
        GL20.glEnableVertexAttribArray(0);
        GL11.glDrawElements(GL11.GL_TRIANGLES, model.getVertexCount(), GL11.GL_UNSIGNED_INT, 0);
        GL20.glDisableVertexAttribArray(0);
        GL30.glBindVertexArray(0);
    }

    @Deprecated
    public void render(TexturedModel model) {
        RawModel rawModel = model.getRawModel();
        GL30.glBindVertexArray(rawModel.getVaoID());
        GL20.glEnableVertexAttribArray(0);
        GL20.glEnableVertexAttribArray(1);
        GL13.glActiveTexture(GL13.GL_TEXTURE1);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, model.getTexture().getID());
        GL11.glDrawElements(GL11.GL_TRIANGLES, rawModel.getVertexCount(), GL11.GL_UNSIGNED_INT, 0);
        GL20.glDisableVertexAttribArray(0);
        GL20.glDisableVertexAttribArray(1);
        GL30.glBindVertexArray(0);
    }

    @Deprecated
    public void render(Entity entity, StaticShader shader) {
        TexturedModel model = entity.getModels().get(0);
        RawModel rawModel = model.getRawModel();
        GL30.glBindVertexArray(rawModel.getVaoID());
        GL20.glEnableVertexAttribArray(0);
        GL20.glEnableVertexAttribArray(1);
        GL20.glEnableVertexAttribArray(2);
        Matrix4f transformationMatrix = Maths.createTransformationMatrix(entity.getPosition(), entity.getRx(), entity.getRy(), entity.getRz(), entity.getScale());
        shader.loadTransformationMatrix(transformationMatrix);
        ModelTexture texture = model.getTexture();
        shader.loadShineVariables(texture.getShineDamper(), texture.getReflectivity());
        GL13.glActiveTexture(GL13.GL_TEXTURE1);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, model.getTexture().getID());
        GL11.glDrawElements(GL11.GL_TRIANGLES, rawModel.getVertexCount(), GL11.GL_UNSIGNED_INT, 0);
        GL20.glDisableVertexAttribArray(0);
        GL20.glDisableVertexAttribArray(1);
        GL20.glDisableVertexAttribArray(2);
        GL30.glBindVertexArray(0);
    }
}