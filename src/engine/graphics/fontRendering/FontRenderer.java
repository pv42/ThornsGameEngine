package engine.graphics.fontRendering;

import engine.graphics.cameras.Camera;
import engine.graphics.fontMeshCreator.FontType;
import engine.graphics.fontMeshCreator.GUIText;
import engine.toolbox.Maths;
import engine.toolbox.Util;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;

import java.util.List;
import java.util.Map;

/***
 * Created by pv42 on 21.06.16.
 */
public class FontRenderer {

    private FontShader shader;
    private float aspectRatio;

    public FontRenderer(float aspectRatio) {
        shader = new FontShader();
        this.aspectRatio = aspectRatio;
    }

    public void render(Map<FontType,List<GUIText>> texts,Camera camera,Matrix4f projectionMatrix) {
        prepare();
        for (FontType font : texts.keySet()) {
            GL13.glActiveTexture(GL13.GL_TEXTURE0);
            GL11.glBindTexture(GL11.GL_TEXTURE_2D, font.getTextureAtlas());
            for(GUIText text : texts.get(font)) {
                renderText(text, camera, projectionMatrix);
            }
        }
        endRendering();
    }
    public void cleanUp() {
        shader.cleanUp();
    }
    private void prepare() {
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glDisable(GL11.GL_DEPTH_TEST);
        shader.start();
    }
    private void renderText(GUIText text, Camera camera, Matrix4f projectionMatrix) {
        GL30.glBindVertexArray(text.getMesh());
        GL20.glEnableVertexAttribArray(0);
        GL20.glEnableVertexAttribArray(1);
        shader.loadColor(text.getColor());
        shader.loadEdge(text.getEdge());
        shader.loadBorderColor(text.getBorderColor());
        shader.loadBorderWidth(text.getBorderWidth());
        Vector2f position = new Vector2f(text.getPosition());
        if(text.isUsePosition3D()) {
            Matrix4f viewMatrix = Maths.createViewMatrix(camera);
            Vector4f worldPosition = Util.getVector4f(text.getPosition3D());
            Vector4f positionToCamera = Matrix4f.transform(viewMatrix , worldPosition,null);
            Vector2f positionOffset = Util.getVector2f(Matrix4f.transform(projectionMatrix , positionToCamera,null));
            Vector2f.add(position,positionOffset,position);
            //todo !!
        }
        Matrix4f matrix = Maths.createTransformationMatrix(position,new Vector2f(1,1));
        Matrix4f.scale(new Vector3f(1/aspectRatio,1,1),matrix,matrix);
        shader.loadTransformation(matrix);
        GL11.glDrawArrays(GL11.GL_TRIANGLES, 0, text.getVertexCount());
        GL20.glDisableVertexAttribArray(0);
        GL20.glDisableVertexAttribArray(1);
        GL30.glBindVertexArray(0);
    }
    private void endRendering(){
        shader.stop();
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glEnable(GL11.GL_DEPTH_TEST);
    }
    //todo!
}
