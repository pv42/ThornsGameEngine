package engine.graphics.particles;

import engine.graphics.cameras.Camera;
import engine.graphics.models.RawModel;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.*;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;
import engine.graphics.renderEngine.Loader;
import engine.toolbox.Maths;
import engine.toolbox.Settings;

import java.nio.FloatBuffer;
import java.util.List;
import java.util.Map;

/**
 * Created by pv42 on 21.06.16.
 */
public class ParticleRenderer {
    private static final float[] VERTECIS = {-0.5f, 0.5f, -0.5f, -0.5f, 0.5f, 0.5f, 0.5f,-0.5f};
    private static final int MAX_INSTANCES = Settings.MAX_PARTICLE_INSTANCES;
    private static final int INSTANCE_DATA_LENGTH =21;
    private static final FloatBuffer buffer = BufferUtils.createFloatBuffer(MAX_INSTANCES * INSTANCE_DATA_LENGTH );
    private RawModel quad;
    private ParticleShader shader;
    private Loader loader;
    private int vbo;
    private int pointer = 0;
    protected ParticleRenderer( Matrix4f projectionMatrix) {
         //quad ...
        this.vbo = loader.createEmptyVbo(INSTANCE_DATA_LENGTH * MAX_INSTANCES);
        quad = loader.loadToVAO(VERTECIS,2);
        Loader.addInstancesAttribute(quad.getVaoID(), vbo, 1 , 4, INSTANCE_DATA_LENGTH, 0);
        Loader.addInstancesAttribute(quad.getVaoID(), vbo, 2 , 4, INSTANCE_DATA_LENGTH, 4);
        Loader.addInstancesAttribute(quad.getVaoID(), vbo, 3 , 4, INSTANCE_DATA_LENGTH, 8);
        Loader.addInstancesAttribute(quad.getVaoID(), vbo, 4 , 4, INSTANCE_DATA_LENGTH, 12);
        Loader.addInstancesAttribute(quad.getVaoID(), vbo, 5 , 4, INSTANCE_DATA_LENGTH, 16);
        Loader.addInstancesAttribute(quad.getVaoID(), vbo, 6 , 1, INSTANCE_DATA_LENGTH, 20);
        shader = new ParticleShader();
        shader.start();
        shader.loadProjectionMatrix(projectionMatrix);
        shader.stop();
    }
    protected void render (Map<ParticleTexture,List<Particle>> particles, Camera camera) {
        Matrix4f viewMatrix = Maths.createViewMatrix(camera);
        prepare();
        for (ParticleTexture texture: particles.keySet()) {
            bindTexture(texture); //// TODO: 23.06.16
            List<Particle> particleList = particles.get(texture);
            pointer = 0;
            float[] vboData = new float[particleList.size() * INSTANCE_DATA_LENGTH];
            for(Particle particle: particleList) {
                updateModelViewMatrix(particle.getPosition(),particle.getRotation(),particle.getScale(),viewMatrix,vboData);
                updateUVInfo(particle,vboData);
            }
            loader.updateVbo(vbo,vboData,buffer);
            GL31.glDrawArraysInstanced(GL11.GL_TRIANGLE_STRIP,0,quad.getVertexCount(),particleList.size());
        }
        finishrendering();
    }
    protected void cleanUp() {
        shader.cleanUp();
    }
    private void updateUVInfo(Particle particle, float[] vboData) {
        vboData[pointer++] = particle.getTexOffset1().x;
        vboData[pointer++] = particle.getTexOffset1().y;
        vboData[pointer++] = particle.getTexOffset2().x;
        vboData[pointer++] = particle.getTexOffset2().y;
        vboData[pointer++] = particle.getBlend();
    }
    private void bindTexture(ParticleTexture texture) {
        if(texture.isAdditive()) {
            GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE);
        } else {
            GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        }
        GL13.glActiveTexture(GL13.GL_TEXTURE0);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D,texture.getTextureID());
        shader.loadNumberOfRows(texture.getNumberOfRows());
    }
    private void updateModelViewMatrix(Vector3f position, float rotation, float scale, Matrix4f viewMatrix,float[] vboData) {
        Matrix4f modelMatrix = new Matrix4f();
        Matrix4f.translate(position,modelMatrix,modelMatrix);
        modelMatrix.m00 = viewMatrix.m00;
        modelMatrix.m01 = viewMatrix.m10;
        modelMatrix.m02 = viewMatrix.m20;
        modelMatrix.m10 = viewMatrix.m01;
        modelMatrix.m11 = viewMatrix.m11;
        modelMatrix.m12 = viewMatrix.m21;
        modelMatrix.m20 = viewMatrix.m02;
        modelMatrix.m21 = viewMatrix.m12;
        modelMatrix.m22 = viewMatrix.m22;
        Matrix4f modelViewMatrix = Matrix4f.mul(viewMatrix, modelMatrix,null);
        Matrix4f.rotate((float)Math.toRadians(rotation),new Vector3f(0,0,1),modelViewMatrix,modelViewMatrix);
        Matrix4f.scale(new Vector3f(scale,scale,scale),modelViewMatrix,modelViewMatrix);
        storeMatrixData(modelViewMatrix,vboData);
    }
    private void storeMatrixData(Matrix4f matrix, float[] data) {
        data[pointer++] = matrix.m00;
        data[pointer++] = matrix.m01;
        data[pointer++] = matrix.m02;
        data[pointer++] = matrix.m03;
        data[pointer++] = matrix.m10;
        data[pointer++] = matrix.m11;
        data[pointer++] = matrix.m12;
        data[pointer++] = matrix.m13;
        data[pointer++] = matrix.m20;
        data[pointer++] = matrix.m21;
        data[pointer++] = matrix.m22;
        data[pointer++] = matrix.m23;
        data[pointer++] = matrix.m30;
        data[pointer++] = matrix.m31;
        data[pointer++] = matrix.m32;
        data[pointer++] = matrix.m33;
    }
    private void prepare() {
        shader.start();
        GL30.glBindVertexArray(quad.getVaoID());
        for (int i = 0; i<=6; i++) {
            GL20.glEnableVertexAttribArray(i);
        }
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glDepthMask(false);
    }
    private void finishrendering() {
        GL11.glDepthMask(true);
        GL11.glDisable(GL11.GL_BLEND);
        for (int i = 0; i<=6; i++) {
            GL20.glDisableVertexAttribArray(i);
        }
        GL30.glBindVertexArray(0);
        shader.stop();
    }

}
