package engine.graphics.normalMappingRenderer;

import java.util.List;
import java.util.Map;

import engine.graphics.cameras.Camera;
import engine.graphics.cameras.ThreeDimensionCamera;
import engine.graphics.glglfwImplementation.models.GLMaterializedModel;
import engine.graphics.glglfwImplementation.textures.GLModelTexture;
import engine.graphics.materials.TexturedMaterial;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.joml.Matrix4f;
import org.joml.Vector4f;

import engine.graphics.glglfwImplementation.entities.GLEntity;
import engine.graphics.lights.Light;
import engine.graphics.glglfwImplementation.models.GLRawModel;
import engine.graphics.glglfwImplementation.MasterRenderer;
import engine.toolbox.Maths;

import static engine.toolbox.Settings.SKY_COLOR;

public class NormalMappingRenderer {

	private NormalMappingShader shader;

	public NormalMappingRenderer(Matrix4f projectionMatrix) {
		this.shader = new NormalMappingShader();
		shader.start();
		shader.loadProjectionMatrix(projectionMatrix);
		shader.connectTextureUnits();
		shader.stop();
	}

	public void updateProjectionMatrix(Matrix4f projectionMatrix) {
		shader.start();
		shader.loadProjectionMatrix(projectionMatrix);
		shader.stop();
	}

	public void render(Map<GLMaterializedModel, List<GLEntity>> entities, Vector4f clipPlane, List<Light> lights, ThreeDimensionCamera camera) {
		shader.start();
		prepare(clipPlane, lights, camera);
		for (GLMaterializedModel model : entities.keySet()) {
			prepareTexturedModel(model);
			List<GLEntity> batch = entities.get(model);
			for (GLEntity entity : batch) {
				prepareInstance(entity);
                GL11.glDrawElements(GL11.GL_TRIANGLES, model.getRawModel().getVertexCount(), GL11.GL_UNSIGNED_INT, 0);
            }
			unbindTexturedModel();
		}
		shader.stop();
	}
	
	public void cleanUp(){
		shader.cleanUp();
	}

	private void prepareTexturedModel(GLMaterializedModel model) {
		GLRawModel rawModel = model.getRawModel();
		GL30.glBindVertexArray(rawModel.getVaoID());
		GL20.glEnableVertexAttribArray(0);
		GL20.glEnableVertexAttribArray(1);
		GL20.glEnableVertexAttribArray(2);
		GL20.glEnableVertexAttribArray(3);
		TexturedMaterial material = (TexturedMaterial)model.getMaterial();
		GLModelTexture texture = (GLModelTexture)material.getTexture();
		shader.loadNumberOfRows(texture.getNumberOfRows());
		if (texture.isHasTransparency()) {
			MasterRenderer.disableCulling();
		}
		shader.loadShineVariables(material.getShineDamper(), material.getReflectivity());
		GL13.glActiveTexture(GL13.GL_TEXTURE0);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, texture.getID());

		GL13.glActiveTexture(GL13.GL_TEXTURE1);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, texture.getNormalMapID());
	}

	private void unbindTexturedModel() {
		MasterRenderer.enableCulling();
		GL20.glDisableVertexAttribArray(0);
		GL20.glDisableVertexAttribArray(1);
		GL20.glDisableVertexAttribArray(2);
		GL20.glDisableVertexAttribArray(3);
		GL30.glBindVertexArray(0);
	}

	private void prepareInstance(GLEntity entity) {
		Matrix4f transformationMatrix = Maths.createTransformationMatrix(entity.getPosition(), entity.getRx(),
				entity.getRy(), entity.getRz(), entity.getScale());
		shader.loadTransformationMatrix(transformationMatrix);
		shader.loadOffset(entity.getTextureXOffset(), entity.getTextureYOffset());
	}

	private void prepare(Vector4f clipPlane, List<Light> lights, Camera camera) {
		shader.loadClipPlane(clipPlane);
		shader.loadSkyColor(SKY_COLOR);
		Matrix4f viewMatrix = camera.getViewMatrix();
		
		shader.loadLights(lights, viewMatrix);
		shader.loadViewMatrix(viewMatrix);
	}

}
