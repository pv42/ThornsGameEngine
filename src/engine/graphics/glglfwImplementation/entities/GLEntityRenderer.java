package engine.graphics.glglfwImplementation.entities;

import engine.graphics.animation.Joint;
import engine.graphics.cameras.Camera;
import engine.graphics.glglfwImplementation.MasterRenderer;
import engine.graphics.glglfwImplementation.models.GLMaterializedModel;
import engine.graphics.glglfwImplementation.models.GLRawModel;
import engine.graphics.glglfwImplementation.shaders.EntityShader;
import engine.graphics.glglfwImplementation.textures.GLModelTexture;
import engine.graphics.lights.Light;
import engine.graphics.materials.Material;
import engine.graphics.materials.TexturedMaterial;
import engine.toolbox.Maths;
import engine.toolbox.Matrix4fDbg;
import org.joml.Matrix4f;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static engine.graphics.glglfwImplementation.GLLoader.VERTEX_ATTRIB_ARRAY_BONE_INDICES;
import static engine.graphics.glglfwImplementation.GLLoader.VERTEX_ATTRIB_ARRAY_BONE_WEIGHT;
import static engine.graphics.glglfwImplementation.GLLoader.VERTEX_ATTRIB_ARRAY_NORMAL;
import static engine.graphics.glglfwImplementation.GLLoader.VERTEX_ATTRIB_ARRAY_POSITION;
import static engine.graphics.glglfwImplementation.GLLoader.VERTEX_ATTRIB_ARRAY_UV;
import static engine.toolbox.Settings.AMBIENT_LIGHT;
import static engine.toolbox.Settings.SKY_COLOR;


/***
 * Created by pv42 on 17.06.16.
 *
 * @author pv42
 */
public class GLEntityRenderer {
    private EntityShader shader;
    private float ambientLight = AMBIENT_LIGHT;

    public GLEntityRenderer(Matrix4f projectionMatrix) {
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

    public void render(Map<List<GLMaterializedModel>, List<GLEntity>> entities, List<Light> lights, Camera camera) {
        prepare(lights, camera);
        for (List<GLMaterializedModel> models : entities.keySet()) {
            for (GLMaterializedModel model : models) {
                prepareTexturedModel(model);
                List<GLEntity> batch = entities.get(models);
                for (GLEntity entity : batch) {
                    prepareInstance(entity);
                    if(model.getMaterial().isWireframe()) {
                        GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_LINE);
                    }
                    GL11.glDrawElements(GL11.GL_TRIANGLES, model.getRawModel().getVertexCount(),
                            GL11.GL_UNSIGNED_INT, 0);
                    GL11.glPolygonMode(GL11.GL_FRONT, GL11.GL_FILL); // set back to normal
                }
                unbindTexturedModel();
            }
        }
        shader.stop();
    }

    private void prepareTexturedModel(GLMaterializedModel model) {
        GLRawModel rawModel = model.getRawModel();
        List<Joint> joints;
        List<Matrix4fDbg> boneMatrices = new ArrayList<>();
        if (model.isAnimated()) {
            joints = rawModel.getJoints();
            for (Joint joint : joints) {
                boneMatrices.add(joint.getTransformationMatrix());
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
        Material material = model.getMaterial();
        if (!(material instanceof TexturedMaterial))
            throw new UnsupportedOperationException("only textured materials are supported");
        GLModelTexture texture = (GLModelTexture) ((TexturedMaterial) material).getTexture();
        shader.loadUseAnimation(model.isAnimated());
        shader.loadTextureAtlasNumberOfRows(texture.getNumberOfRows());
        if (texture.isHasTransparency()) {
            MasterRenderer.disableCulling();
        }
        shader.loadShineVariables(material.getShineDamper(), material.getReflectivity());
        shader.loadFakeLightning(texture.isUseFakeLightning());
        if (model.isAnimated()) shader.loadBones(boneMatrices);
        GL13.glActiveTexture(GL13.GL_TEXTURE0);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, texture.getID());
        shader.loadUseSpecMap(texture.hasSpecularMap());
        if (texture.hasSpecularMap()) {
            GL13.glActiveTexture(GL13.GL_TEXTURE1);
            GL11.glBindTexture(GL11.GL_TEXTURE_2D, texture.getSpecularMapID());
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

    private void prepareInstance(GLEntity entity) {
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

    public void setAmbientLight(float ambientLight) {
        this.ambientLight = ambientLight;
    }
}