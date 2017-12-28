package engine.graphics.renderEngine;

import engine.graphics.animation.Joint;
import engine.graphics.cameras.Camera;
import engine.graphics.cameras.ThreeDimensionCamera;
import engine.graphics.entities.Entity;
import engine.graphics.lights.Light;
import engine.graphics.models.RawModel;
import engine.graphics.models.TexturedModel;
import engine.graphics.shaders.EntityShader;
import org.lwjgl.opengl.*;
import org.joml.Matrix4f;
import engine.graphics.textures.ModelTexture;
import engine.toolbox.Maths;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static engine.graphics.renderEngine.Loader.VERTEX_ATTRIB_ARRAY_POSITION;
import static engine.graphics.renderEngine.Loader.VERTEX_ATTRIB_ARRAY_UV;
import static engine.graphics.renderEngine.Loader.VERTEX_ATTRIB_ARRAY_NORMAL;
import static engine.graphics.renderEngine.Loader.VERTEX_ATTRIB_ARRAY_BONE_INDICES;
import static engine.graphics.renderEngine.Loader.VERTEX_ATTRIB_ARRAY_BONE_WEIGHT;
import static engine.toolbox.Settings.AMBIENT_LIGHT;
import static engine.toolbox.Settings.SKY_COLOR;


/***
 * Created by pv42 on 17.06.16.
 */
public class EntityRenderer {
    private EntityShader shader;
    private float ambientLight = AMBIENT_LIGHT;

    public EntityRenderer(Matrix4f projectionMatrix) {
        this.shader = new EntityShader();
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
        List<Joint> joints;
        List<Matrix4f> boneMatrices = new ArrayList<>();
        if (model.isAnimated()) {
            joints = rawModel.getJoints();
            for(Joint joint : joints) {
                boneMatrices.add(joint.getJointMatrix());
            }
        }
        GL30.glBindVertexArray(rawModel.getVaoID());
        GL20.glEnableVertexAttribArray(VERTEX_ATTRIB_ARRAY_POSITION);
        GL20.glEnableVertexAttribArray(VERTEX_ATTRIB_ARRAY_UV);
        GL20.glEnableVertexAttribArray(VERTEX_ATTRIB_ARRAY_NORMAL);
        if (model.isAnimated()) {
            GL20.glEnableVertexAttribArray(VERTEX_ATTRIB_ARRAY_BONE_INDICES);
            GL20.glEnableVertexAttribArray(VERTEX_ATTRIB_ARRAY_BONE_WEIGHT);
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
        GL20.glDisableVertexAttribArray(VERTEX_ATTRIB_ARRAY_BONE_INDICES); //ani
        GL20.glDisableVertexAttribArray(VERTEX_ATTRIB_ARRAY_BONE_WEIGHT); //ani
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
        shader.loadAmbientLight(ambientLight);
        shader.loadLights(lights);
        shader.loadViewMatrix(camera.getViewMatrix());
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
    public void render(Entity entity, EntityShader shader) {
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

    public void setAmbientLight(float ambientLight) {
        this.ambientLight = ambientLight;
    }
}