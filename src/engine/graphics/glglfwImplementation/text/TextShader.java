package engine.graphics.glglfwImplementation.text;

import engine.graphics.shaders.ShaderProgram;
import engine.toolbox.Color;
import org.joml.Vector4f;


/**
 * a binding class for the OpenGL shaders for text rendering
 *
 * @author pv42
 */
public class TextShader extends ShaderProgram {
    private static final String FRAGMENT_SHADER = "textFragmentShader";
    private static final String VERTEX_SHADER = "textVertexShader";
    private int textColorLocation;
    private int glyphUVLocation;
    private int targetPosLocation;
    private int textureLocation;


    /**
     * creates a text shader
     */
    TextShader() {
        super(VERTEX_SHADER, FRAGMENT_SHADER);
    }

    /**
     * binds a vbo to a shader in attribute
     */
    @Override
    protected void bindAttributes() {
        bindAttribute(0, "position");
    }

    /**
     * gets a uniform location addresses from the shader
     */
    @Override
    protected void getAllUniformLocations() {
        textColorLocation = getUniformLocation("textColor");
        glyphUVLocation = getUniformLocation("glyphUV");
        targetPosLocation = getUniformLocation("targetPos");
        textureLocation = getUniformLocation("texture");
    }

    /**
     * loads the text color to a uniform for the glsl shader to use, alpha is supported
     *
     * @param textColor text color to load
     */
    void loadTextColor(Color textColor) {
        Vector4f color = new Vector4f(
                textColor.getR(),
                textColor.getG(),
                textColor.getB(),
                textColor.getA());
        loadVector(textColorLocation, color);
    }

    /**
     * loads the glyphs position on the font texture map in uv-coordinates (also often referred as st-coordinated)
     *
     * @param umin minimum u coordinate
     * @param vmin minimum v coordinate
     * @param umax maximum u coordinate
     * @param vmax maximum v coordinate
     */
    void loadGlyphUV(float umin, float vmin, float umax, float vmax) {
        loadVector(glyphUVLocation, new Vector4f(umin, vmin, umax, vmax));
    }

    /**
     * load the position boundaries , where the glyph should be rendered (in OpenGL NDC [-1,1]x[-1,1])
     *
     * @param xmin minimum x coordinate
     * @param ymin minimum y coordinate
     * @param xmax maximum x coordinate
     * @param ymax maximum y coordinate
     */
    void loadPosition(float xmin, float ymin, float xmax, float ymax) {
        loadVector(targetPosLocation, new Vector4f(xmin, ymin, xmax, ymax));
    }

    /**
     * connects the shader uniform to texture unit 0
     */
    void connectTextures() {
        super.loadInt(textureLocation, 0);
    }
}
