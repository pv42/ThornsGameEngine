package engine.graphics.fontRendering;

import engine.toolbox.Color;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;
import engine.graphics.shaders.ShaderProgram;

public class FontShader extends ShaderProgram{

	private static final String VERTEX_FILE = "src/engine/graphics/fontRendering/fontVertex";
	private static final String FRAGMENT_FILE = "src/engine/graphics/fontRendering/fontFragment";


	private int location_color;
	private int location_borderColor;
	private int location_transformation;
    private int location_borderWidth;
    private int location_edge;
	public FontShader() {
		super(VERTEX_FILE, FRAGMENT_FILE);
	}

	@Override
	protected void getAllUniformLocations() {
		location_color = super.getUniformLocation("color");
		location_transformation = super.getUniformLocation("transformationMatrix");
		location_borderColor = super.getUniformLocation("borderColor");
        location_borderWidth = super.getUniformLocation("borderWidth");
	    location_edge = super.getUniformLocation("edge");
    }

	@Override
	protected void bindAttributes() {
		super.bindAttribute(0, "position");
		super.bindAttribute(1, "uv");
	}
	protected void loadColor(Color color) {
		super.loadVector(location_color, color.getVector());
	}
	protected void loadBorderColor(Vector3f color) {
		super.loadVector(location_borderColor, color);
	}
	protected void loadTransformation(Matrix4f transformation) {
		super.loadMatrix(location_transformation,transformation);
	}
    protected void loadBorderWidth(float borderWidth) {
        super.loadFloat(location_borderWidth,borderWidth);
    }
    protected void loadEdge(float edge) {
        super.loadFloat(location_edge,edge);
    }


}
