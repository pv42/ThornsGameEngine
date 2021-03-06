package engine.graphics.glglfwImplementation.guis;


import engine.graphics.glglfwImplementation.GLLoader;
import engine.graphics.glglfwImplementation.models.GLRawModel;
import org.joml.Vector3f;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.joml.Matrix4f;
import engine.toolbox.Maths;

import java.util.List;

/***
 * Created by pv42 on 20.06.16.
 */
public class GuiRenderer {
    private final GLRawModel quad;
    private float aspectRatio;
    private GuiShader shader;
    public GuiRenderer(float aspectRatio) {
        float[] positions = {-1, 1, -1, -1, 1, 1, 1,-1};
        quad = GLLoader.loadToVAO(positions,2);
        this.aspectRatio = aspectRatio;
        shader = new GuiShader();
    }
    public void render(List<GuiTexture> guis) {
        shader.start();
        GL30.glBindVertexArray(quad.getVaoID());
        GL20.glEnableVertexAttribArray(0);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glDisable(GL11.GL_DEPTH_TEST);
        for (GuiTexture gui: guis) {
            GL13.glActiveTexture(GL13.GL_TEXTURE0);
            GL11.glBindTexture(GL11.GL_TEXTURE_2D, gui.getTexture());
            Matrix4f matrix = Maths.createTransformationMatrix(gui.getPosition(),gui.getScale());
            matrix.scale(new Vector3f(1/aspectRatio,1,1));
            shader.loadTransformationMatrix(matrix);
            GL11.glDrawArrays(GL11.GL_TRIANGLE_STRIP,0 , quad.getVertexCount());
        }
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glDisable(GL11.GL_BLEND);
        GL20.glDisableVertexAttribArray(0);
        GL30.glBindVertexArray(0);
        shader.stop();
    }
}
