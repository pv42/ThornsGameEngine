package engine.graphics.glglfwImplementation.text;

import engine.graphics.glglfwImplementation.models.GLRawModel;
import engine.graphics.glglfwImplementation.GLLoader;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.stb.STBTTAlignedQuad;
import org.lwjgl.stb.STBTruetype;

/**
 * A Class to render 2D Text with a TrueTypeFont using OpenGL and Sean's Tool Box (STB_TrueType),
 * part of the render engine
 *
 * @author pv42
 */
public class GLTextRenderer {
    private final GLRawModel quadModel;
    private TextShader shader;
    private GLTTFont font;
    private float size;
    private float currentXPos; // in NDC
    private float currentYPos; // in NDC
    private float[] xPos = new float[1];
    private float[] yPos = new float[1];
    private STBTTAlignedQuad quad = STBTTAlignedQuad.create();

    /**
     * creates the renderer, loads the shader, the vao, connects the texture bank
     */
    public GLTextRenderer() {
        shader = new TextShader();
        float[] position = {-1, 1, -1, -1, 1, 1, 1, -1};
        quadModel = GLLoader.loadToVAO(position, 2); // todo fix reinitialisation of the model
        shader.start();
        shader.connectTextures();
        shader.stop();
    }

    /**
     * renders a GLGuiText, called from the render engine
     *
     * @param text        guiText to be rendered
     * @param aspectRatio the render target's (e.g. window's) aspect ratio
     */
    public void renderText(GLGuiText text, float aspectRatio) {
        GL11.glDisable(GL11.GL_DEPTH_TEST);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        font = text.getFont();
        size = text.getSize();
        currentXPos = text.getPosition().x();
        currentYPos = text.getPosition().y();
        shader.start();
        shader.loadTextColor(text.getColor());
        GL30.glBindVertexArray(quadModel.getVaoID());
        GL20.glEnableVertexAttribArray(0);
        GL13.glActiveTexture(GL13.GL_TEXTURE0);
        int guiTextureID = font.getTexture();
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, guiTextureID);
        for (char c : text.getString().toCharArray()) {
            renderChar(c, aspectRatio);
        }
        GL20.glDisableVertexAttribArray(0);
        GL30.glBindVertexArray(0);
        shader.stop();
        GL11.glEnable(GL11.GL_DEPTH_TEST);

    }

    /**
     * renders a single character at the cursor position with the properties defined by the renderText's text parameter,
     * increases the cursor's position
     *
     * @param c           character to render
     * @param aspectRatio aspect ratio of the window to render to
     */
    private void renderChar(char c, float aspectRatio) {
        xPos[0] = 0;
        yPos[0] = 0;
        //Log.i("STBCall::GetBakedQuad", "c=" + c + " co=" + (c - GLTTFont.CODEPOINT_OFFSET));
        STBTruetype.stbtt_GetBakedQuad(font.getBakedBuffer(), font.getBitmapSize(), font.getBitmapSize(),
                c - GLTTFont.CODEPOINT_OFFSET, xPos, yPos, quad, true);
        float posMul = size / font.getScale(); // c, use 1k for sung mung
        currentXPos += font.getLeftSideBearing(c) * size;
        float x0 = currentXPos + quad.x0() * posMul / aspectRatio;
        float y0 = currentYPos - quad.y1() * posMul;
        float x1 = currentXPos + quad.x1() * posMul / aspectRatio;
        float y1 = currentYPos - quad.y0() * posMul;
        //currentYPos = quad.y0();
        //Log.i("QUAD", quad.x0() +"<->" +quad.x1()+" | "+quad.y0() + "<->" +quad.y1());
        //Log.i("RQ", x0 +"<->" +x1+" | "+y0 + "<->" +y1);
        shader.loadPosition(x0, y0, x1, y1);
        shader.loadGlyphUV(quad.s0(), quad.t0(), quad.s1(), quad.t1());
        GL11.glDrawArrays(GL11.GL_TRIANGLE_STRIP, 0, quadModel.getVertexCount());
        currentXPos += font.getAdvancedWidth(c) * size / aspectRatio;
    }

    /**
     * cleans up the text renderer, mainly its shader
     */
    public void cleanUp() {
        shader.cleanUp();
    }

}
