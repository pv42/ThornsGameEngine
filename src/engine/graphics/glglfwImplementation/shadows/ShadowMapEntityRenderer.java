package engine.graphics.glglfwImplementation.shadows;

import engine.graphics.animation.Joint;
import engine.graphics.glglfwImplementation.entities.GLEntity;
import engine.graphics.glglfwImplementation.models.GLMaterializedModel;
import engine.graphics.glglfwImplementation.models.GLRawModel;
import engine.graphics.glglfwImplementation.shaders.ShadowShader;
import engine.toolbox.Maths;
import org.joml.Matrix4f;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static engine.graphics.glglfwImplementation.GLLoader.VERTEX_ATTRIB_ARRAY_BONE_INDICES;
import static engine.graphics.glglfwImplementation.GLLoader.VERTEX_ATTRIB_ARRAY_BONE_WEIGHT;
import static engine.graphics.glglfwImplementation.GLLoader.VERTEX_ATTRIB_ARRAY_POSITION;

public class ShadowMapEntityRenderer {

    private Matrix4f projectionViewMatrix;
    private ShadowShader shader;

    /**
     * @param shader               the simple shader program being used for the shadow render pass.
     * @param projectionViewMatrix the orthographic projection matrix multiplied by the light's "view" matrix.
     */
    protected ShadowMapEntityRenderer(ShadowShader shader, Matrix4f projectionViewMatrix) {
        this.shader = shader;
        this.projectionViewMatrix = projectionViewMatrix;
    }

    /**
     * Renders entieis to the shadow map. Each model is first bound and then all
     * of the entities using that model are rendered to the shadow map.
     *
     * @param entities - the entities to be rendered to the shadow map.
     */
    protected void render(Map<List<GLMaterializedModel>, List<GLEntity>> entities) {
        for (List<GLMaterializedModel> models : entities.keySet()) {
            for (GLMaterializedModel model : models) {
                prepareMaterializedModel(model);
                GLRawModel rawModel = model.getRawModel();
                for (GLEntity entity : entities.get(models)) {
                    prepareInstance(entity);
                    GL11.glDrawElements(GL11.GL_TRIANGLES, rawModel.getVertexCount(),
                            GL11.GL_UNSIGNED_INT, 0);
                }
            }
        }
        GL20.glDisableVertexAttribArray(VERTEX_ATTRIB_ARRAY_POSITION);
        GL20.glDisableVertexAttribArray(VERTEX_ATTRIB_ARRAY_BONE_INDICES);
        GL20.glDisableVertexAttribArray(VERTEX_ATTRIB_ARRAY_BONE_WEIGHT);
        GL30.glBindVertexArray(0);
    }

    /**
     * Prepares an entity to be rendered. The model matrix is created in the
     * usual way and then multiplied with the projection and view matrix (often
     * in the past we've done this in the vertex shader) to create the
     * mvp-matrix. This is then loaded to the vertex shader as a uniform.
     *
     * @param entity the entity to be prepared for rendering.
     */
    private void prepareInstance(GLEntity entity) {
        Matrix4f modelMatrix = Maths.createTransformationMatrix(entity.getPosition(),
                entity.getRx(), entity.getRy(), entity.getRz(), entity.getScale());
        Matrix4f mvpMatrix = projectionViewMatrix.mul(modelMatrix, new Matrix4f());
        shader.loadMVPMatrix(mvpMatrix);
    }

    /**
     * Binds a model before rendering. Only the attribute for posiont and if present for bone indices/weights are
     * enabled here because that is where the positions are stored in the VAO, and only the positions are required in
     * the vertex shader.
     *
     * @param model - the model to be bound.
     */
    private void prepareMaterializedModel(GLMaterializedModel model) {
        GLRawModel rawModel = model.getRawModel();
        List<Matrix4f> boneMatrices = new ArrayList<>();
        if (model.isAnimated()) {
            List<Joint> joints = rawModel.getJoints();
            for (Joint joint : joints) {
                boneMatrices.add(joint.getTransformationMatrix());
            }
        }
        if (model.isAnimated()) shader.loadBones(boneMatrices);
        shader.loadUseAnimation(model.isAnimated());
        GL30.glBindVertexArray(rawModel.getVaoID());
        GL20.glEnableVertexAttribArray(VERTEX_ATTRIB_ARRAY_POSITION);
        if (model.isAnimated()) {
            GL20.glEnableVertexAttribArray(VERTEX_ATTRIB_ARRAY_BONE_INDICES);
            GL20.glEnableVertexAttribArray(VERTEX_ATTRIB_ARRAY_BONE_WEIGHT);
        }
    }

}
