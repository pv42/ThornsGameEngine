package engine.graphics.particles;

import org.joml.Matrix4f;

import engine.graphics.shaders.ShaderProgram;

public class ParticleShader extends ShaderProgram {

	private static final String VERTEX_FILE = "particleVShader";
	private static final String FRAGMENT_FILE = "particleFShader";

	private int location_projectionMatrix;
	private int location_numberOfRows;


	public ParticleShader() {
		super(VERTEX_FILE, FRAGMENT_FILE);
	}

	@Override
	protected void getAllUniformLocations() {
		location_projectionMatrix = super.getUniformLocation("projectionMatrix");
		location_numberOfRows = super.getUniformLocation("numOfRows");
	}

	@Override
	protected void bindAttributes() {
        super.bindAttribute(0, "position");
		super.bindAttribute(1, "modelViewMatrix");
		super.bindAttribute(5, "uv_Offsets");
		super.bindAttribute(6, "blendFactor");
    }


	protected void loadProjectionMatrix(Matrix4f projectionMatrix) {
		super.loadMatrix(location_projectionMatrix, projectionMatrix);
	}
	protected void loadNumberOfRows(float numOfRows) {

		super.loadFloat(location_numberOfRows,numOfRows);
	}

}
