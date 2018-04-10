package engine.graphics.lines;

import engine.graphics.cameras.Camera;
import engine.graphics.cameras.ThreeDimensionCamera;
import engine.graphics.renderEngine.MasterRenderer;
import engine.toolbox.Maths;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.joml.Matrix4f;
import java.util.List;

/***
 * Created by pv42 on 12.08.16.
 */

public class LineRenderer {
    private LineShader shader;
    public LineRenderer(Matrix4f projectionMatrix) {
        this.shader = new LineShader();
        shader.start();
        shader.loadProjectionMatrix(projectionMatrix);
        shader.stop();
    }
    public void updateProjectionMatrix(Matrix4f projectionMatrix) {
        shader.start();
        shader.loadProjectionMatrix(projectionMatrix);
        shader.stop();
    }
    public void render(List<LineModel> lineStrips, Camera camera) {
        shader.start();
        shader.loadViewMatrix(camera);
        GL11.glDisable(GL11.GL_DEPTH_TEST);
        MasterRenderer.disableCulling();
        GL11.glLineWidth(20);
        for (LineModel lineStripModel: lineStrips) {

            prepareLineStrip(lineStripModel);

            GL11.glDrawElements(GL11.GL_LINES, lineStripModel.getPointCount(),
                    GL11.GL_UNSIGNED_INT, 0);
            unbindTexturedModel();
        }
        MasterRenderer.enableCulling();
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        shader.stop();
    }
    private void prepareLineStrip(LineModel model) {
        loadModelMatrix(model);
        shader.loadColor(model.getColor());
        GL30.glBindVertexArray(model.getVaoID());
        GL20.glEnableVertexAttribArray(0);
    }
    private void unbindTexturedModel() {
        GL20.glDisableVertexAttribArray(0);
        GL30.glBindVertexArray(0);
    }
    private void loadModelMatrix(LineModel lineStripModel) {
        Matrix4f transformationMatrix = Maths.createTransformationMatrix(
                lineStripModel.getPosition(), lineStripModel.getRotation(), lineStripModel.getScale());
        shader.loadTransformationMatrix(transformationMatrix.mul(lineStripModel.getTransformation(),new Matrix4f()));
    }
}
