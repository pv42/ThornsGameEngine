package engine.graphics.skybox;

import engine.EngineMaster;
import engine.graphics.cameras.Camera;
import engine.graphics.glglfwImplementation.GLLoader;
import engine.graphics.glglfwImplementation.models.GLRawModel;
import engine.toolbox.Color;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.joml.Matrix4f;

/**
 * Created by pv42 on 21.06.16.
 */
public class SkyboxRenderer {
    private static final float SIZE = 1000f;
    private static final float[] VERTICES = {
            -SIZE, SIZE, -SIZE, //0
            -SIZE, -SIZE, -SIZE,
            SIZE, -SIZE, -SIZE,
            SIZE, -SIZE, -SIZE,
            SIZE, SIZE, -SIZE,
            -SIZE, SIZE, -SIZE,

            -SIZE, -SIZE, SIZE, //1
            -SIZE, -SIZE, -SIZE,
            -SIZE, SIZE, -SIZE,
            -SIZE, SIZE, -SIZE,
            -SIZE, SIZE, SIZE,
            -SIZE, -SIZE, SIZE,

            SIZE, -SIZE, -SIZE, //2
            SIZE, -SIZE, SIZE,
            SIZE, SIZE, SIZE,
            SIZE, SIZE, SIZE,
            SIZE, SIZE, -SIZE,
            SIZE, -SIZE, -SIZE,

            -SIZE, -SIZE, SIZE, //3
            -SIZE, SIZE, SIZE,
            SIZE, SIZE, SIZE,
            SIZE, SIZE, SIZE,
            SIZE, -SIZE, SIZE,
            -SIZE, -SIZE, SIZE,

            -SIZE, SIZE, -SIZE, //4
            SIZE, SIZE, -SIZE,
            SIZE, SIZE, SIZE,
            SIZE, SIZE, SIZE,
            -SIZE, SIZE, SIZE,
            -SIZE, SIZE, -SIZE,

            -SIZE, -SIZE, -SIZE, //5
            -SIZE, -SIZE, SIZE,
            SIZE, -SIZE, -SIZE,
            SIZE, -SIZE, -SIZE,
            -SIZE, -SIZE, SIZE,
            SIZE, -SIZE, SIZE
    };
    private GLRawModel cube;
    private static SkyBoxShader shader;
    private int texture;
    public SkyboxRenderer(Matrix4f projectionMatrix,String textureFile, String extension) {
        cube = GLLoader.loadToVAO(VERTICES, 3);
        if (extension == null) extension = ".png";
        texture = EngineMaster.getTextureLoader().loadCubeMapTexture(textureFile, extension);
        shader = new SkyBoxShader();
        shader.start();
        shader.connectTextures();
        shader.loadProjectionMatrix(projectionMatrix);
        shader.stop();
    }
    public SkyboxRenderer(Matrix4f projectionMatrix,String textureFile) {
        this(projectionMatrix, textureFile, null);
    }

    public void updateProjectionMatrix(Matrix4f projectionMatrix) {
        shader.start();
        shader.loadProjectionMatrix(projectionMatrix);
        shader.stop();
    }

    public void render(Camera camera, Color fogColor, float timeDelta) {
        shader.start();
        shader.loadViewMatrix(camera, timeDelta);
        shader.loadFogColor(fogColor);
        GL30.glBindVertexArray(cube.getVaoID());
        GL20.glEnableVertexAttribArray(0);
        bindTextures();
        GL11.glDrawArrays(GL11.GL_TRIANGLES, 0 , cube.getVertexCount());
        GL20.glDisableVertexAttribArray(0);
        GL30.glBindVertexArray(0);
        shader.stop();
    }
    private void bindTextures() {
        GL13.glActiveTexture(GL13.GL_TEXTURE0);
        GL11.glBindTexture(GL13.GL_TEXTURE_CUBE_MAP, texture);
    }

    public void cleanUp() {
        shader.cleanUp();
    }
}
