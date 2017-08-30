package engine.graphics.shaders;

public class SimpleShader extends ShaderProgram {
    private static final String VERTEX_FILE = "src/engine/graphics/shaders/glsl/simpleVertexShader.glsl";
    private static final String FRAGMENT_FILE = "src/engine/graphics/shaders/glsl/testFragmentShader.glsl";

    public SimpleShader() {
        super(VERTEX_FILE, FRAGMENT_FILE);
    }

    @Override
    protected void bindAttributes() {
        super.bindAttribute(0, "position");
        super.bindAttribute(1, "uv");
    }

    @Override
    protected void getAllUniformLocations() {

    }
}
