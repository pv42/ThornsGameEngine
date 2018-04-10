package engineTester.graphicTest;

import engine.graphics.cameras.Camera;
import engine.graphics.cameras.ThreeDimensionCamera;
import engine.graphics.entities.Entity;
import engine.graphics.models.RawModel;
import engine.graphics.models.TexturedModel;
import engine.graphics.shaders.EntityShader;
import engine.toolbox.Maths;
import engine.toolbox.Settings;
import org.joml.Matrix4f;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

public class TestRender {
    private EntityShader shader;
    private Matrix4f projectionMatrix;
    private boolean f = true;
    public TestRender() {
        this.shader = new EntityShader();
        createProjectionMatrix(1,1);
    }

    private void createProjectionMatrix(float aspectRatio, float zoom) {
        float y_scale = (float) ((1f / Math.tan(Math.toRadians(Settings.FOV / zoom / 2f))));
        float x_scale = y_scale / aspectRatio;
        float frustum_length = Settings.FAR_PLANE - Settings.NEAR_PLANE;

        projectionMatrix = new Matrix4f();
        projectionMatrix.m00(x_scale);
        projectionMatrix.m11(y_scale);
        projectionMatrix.m22(-((Settings.FAR_PLANE + Settings.NEAR_PLANE) / frustum_length));
        projectionMatrix.m23(-1);
        projectionMatrix.m32(-((2 * Settings.NEAR_PLANE * Settings.FAR_PLANE) / frustum_length));
        projectionMatrix.m33(0);
        System.out.println("PRM \n" + projectionMatrix.toString());
    }

    public void prepare() {
        GL11.glClearColor(.4f,0.2f,0,1);
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT);
    }
    public void render(Entity entity, Camera camera) {
        if(f) {
            System.out.println("VM:\n" + camera.getViewMatrix());
            System.out.println("TM:\n" + Maths.createTransformationMatrix(entity.getPosition(),
                    entity.getRx(), entity.getRy(), entity.getRz(), 1));
            f = false;
        }
        shader.start();
        TexturedModel texturedModel = entity.getModels().get(0);
        RawModel model = texturedModel.getRawModel();
        shader.loadProjectionMatrix(projectionMatrix);
        shader.loadViewMatrix(camera.getViewMatrix());
        shader.loadTransformationMatrix(Maths.createTransformationMatrix(entity.getPosition(),
                entity.getRx(), entity.getRy(), entity.getRz(), entity.getScale()));
        GL30.glBindVertexArray(model.getVaoID());
        GL20.glEnableVertexAttribArray(0);
        GL20.glEnableVertexAttribArray(1);
        GL13.glActiveTexture(GL13.GL_TEXTURE0);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, texturedModel.getTexture().getID());
        GL11.glDrawElements(GL11.GL_TRIANGLES, model.getVertexCount(), GL11.GL_UNSIGNED_INT, 0);
        GL20.glDisableVertexAttribArray(0);
        GL20.glDisableVertexAttribArray(1);
        GL30.glBindVertexArray(0);
        shader.stop();
    }

    public void cleanUp() {
        shader.cleanUp();
    }
}
