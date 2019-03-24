package engine.graphics.glglfwImplementation.entities;

import engine.graphics.animation.Joint;
import engine.graphics.cameras.Camera;
import engine.graphics.glglfwImplementation.MasterRenderer;
import engine.graphics.glglfwImplementation.models.GLRawModel;
import engine.graphics.glglfwImplementation.models.GLTexturedModel;
import engine.graphics.glglfwImplementation.textures.ModelTexture;
import engine.graphics.lights.Light;
import engine.graphics.shaders.EntityShader;
import engine.toolbox.Maths;
import engine.toolbox.Matrix4fDbg;
import org.joml.Matrix4f;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

import java.text.DecimalFormat;
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
    private static boolean already_printed = false;
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

    public void render(Map<List<GLTexturedModel>, List<GLEntity>> entities, List<Light> lights, Camera camera) {
        prepare(lights, camera);
        for (List<GLTexturedModel> models : entities.keySet()) {
            for (GLTexturedModel model : models) {
                prepareTexturedModel(model);
                List<GLEntity> batch = entities.get(models);
                for (GLEntity entity : batch) {
                    prepareInstance(entity);
                    GL11.glDrawElements(GL11.GL_TRIANGLES, model.getRawModel().getVertexCount(),
                            GL11.GL_UNSIGNED_INT, 0);
                }
                unbindTexturedModel();
            }
        }
        shader.stop();
    }

    private void prepareTexturedModel(GLTexturedModel model) {
        GLRawModel rawModel = model.getRawModel();
        List<Joint> joints;
        List<Matrix4fDbg> boneMatrices = new ArrayList<>();
        if (model.isAnimated()) {
            joints = rawModel.getJoints();
            System.out.print(joints);
            int i = 0;

            for (Joint joint : joints) {
                //matrix is supposed to be  root.proc * child.proc * ... * leaf.proc * (root.mdj * child.mdj * ... * leaf.mdj) ^(-1)
                // = root.proc * child.proc * ... * leaf.proc * (leaf.mdj ^-1 * ... * child.mdj^-1 * root.mdj^-1)
                // proc == IBM
                if (!already_printed) {
                    if(joint == null) continue;
                    System.out.println("[" + i + "]" + joint.getId() + ": " + joint.getTransformationMatrix().getName());
                    System.out.println(joint.getTransformationMatrix().toString(new DecimalFormat()));
                    i++;
                }
                boneMatrices.add(joint.getTransformationMatrix());
            }
            already_printed = true;
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
        shader.loadTextureAtlasNumberOfRows(texture.getNumberOfRows());
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

    @Deprecated
    public void render(GLRawModel model) {
        GL30.glBindVertexArray(model.getVaoID());
        GL20.glEnableVertexAttribArray(0);
        GL11.glDrawElements(GL11.GL_TRIANGLES, model.getVertexCount(), GL11.GL_UNSIGNED_INT, 0);
        GL20.glDisableVertexAttribArray(0);
        GL30.glBindVertexArray(0);
    }

    @Deprecated
    public void render(GLTexturedModel model) {
        GLRawModel rawModel = model.getRawModel();
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
    public void render(GLEntity entity, EntityShader shader) {
        GLTexturedModel model = entity.getModels().get(0);
        GLRawModel rawModel = model.getRawModel();
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