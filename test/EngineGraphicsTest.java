import engine.graphics.cameras.StaticThreeDimensionCamera;
import engine.graphics.cameras.ThreeDimensionCamera;
import engine.graphics.glglfwImplementation.display.GLFWDisplayManager;
import engine.graphics.glglfwImplementation.entities.GLEntity;
import engine.graphics.glglfwImplementation.models.GLRawModel;
import engine.graphics.lights.Light;
import engine.toolbox.Log;
import engine.toolbox.OBJLoader;
import engine.graphics.glglfwImplementation.models.GLTexturedModel;
import engine.graphics.glglfwImplementation.GLLoader;
import engine.graphics.glglfwImplementation.MasterRenderer;
import engine.graphics.shaders.EntityShader;
import engine.graphics.glglfwImplementation.textures.ModelTexture;
import engine.toolbox.Color;
import engine.toolbox.Maths;
import engine.toolbox.Settings;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.junit.jupiter.api.Test;
import org.lwjgl.BufferUtils;
import org.lwjgl.glfw.*;
import org.lwjgl.opengl.*;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import static engine.toolbox.Settings.SKY_COLOR;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;

/**
 * Created by pv42 on 11.07.2017.
 */
public class EngineGraphicsTest {
    private static Matrix4f projectionMatrix;
    private static int progress = 1;
    private static float time = 1;
    @Test
    public void main() {
        //EngineMaster.init();
        //int texture = GLLoader.loadTexture("grass.png");
        //GLTexturedModel model = new GLTexturedModel(OBJLoader.loadObjModel("dragon"), new ModelTexture(texture));
        //GLEntity entity = new GLEntity(model,new Vector3f(0,0,0),0,0,0,1);
        //ThreeDimensionCamera camera = new StaticThreeDimensionCamera(new Vector3f(-10,0,0), new Vector3f(0,0,0));
        long window;
        GLFWErrorCallback errorCallback = GLFWErrorCallback.createPrint().set();
        if (!glfwInit()) {
            throw new IllegalStateException("Unable to initialize GLFW");
        }
        GLFWDisplayManager displayManager = new GLFWDisplayManager();
        window = displayManager.createWindow().getId();
        //glMatrixMode(GL_PROJECTION);
        //glLoadIdentity();
        glMatrixMode(GL_MODELVIEW);

        ByteBuffer charBuffer = BufferUtils.createByteBuffer(256 * 270);

        glEnableClientState(GL_VERTEX_ARRAY);
        glVertexPointer(2, GL_FLOAT, 16, charBuffer);

        glClearColor(43f / 255f, 43f / 255f, 43f / 255f, 0f); // BG color
        GLRawModel rawModel = OBJLoader.loadObjModel("barrel");
        //GLRawModel rawModel = OBJLoader.loadObjModel("bunny");
        createProjectionMatrix(1,1);
        EntityShader shader = new EntityShader();
        ThreeDimensionCamera camera = new StaticThreeDimensionCamera(new Vector3f(0,0,20), new Vector3f());
        Light cameraLight = new Light(new Vector3f(0,0,10), new Color(1.0,1.0,1.0));
        List<Light> lights = new ArrayList<>();
        lights.add(cameraLight);
        ModelTexture texture = new ModelTexture(GLLoader.loadTexture("barrel.png"));
        GLTexturedModel texturedModel = new GLTexturedModel(rawModel,texture);
        GLEntity entity = new GLEntity(texturedModel,new Vector3f());
        //entity.setScale(.2f);
        entity.setPosition(0,-1,-1);
        prepareRenderer(shader,projectionMatrix);
        GL11.glEnable(GL_COLOR_BUFFER_BIT);
        GL11.glEnable(GL_DEPTH_TEST);
        GL11.glDepthFunc(GL_LEQUAL);
        int count = 0;
        while (!glfwWindowShouldClose(window) && count < 60) {
            glfwPollEvents();
            glClearDepth(1.0);
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
            glClearColor(0.1f,0.4f,0.7f, 0.5f);
            entity.increaseRotation(new Vector3f(.1f,.07f,.03f).mul(1.f));
            render(entity,shader,camera,lights);
            glfwSwapBuffers(window);
            count ++;
        }
        displayManager.cleanUp();
        glfwTerminate();
        glfwSetErrorCallback(null).free();
        assertEquals(0, Log.getErrorNumber());
        assertEquals(0, Log.getWarningNumber());
    }

    private void prepareRenderer(EntityShader shader, Matrix4f projectionMatrix) {
        shader.start();
        shader.loadProjectionMatrix(projectionMatrix);
        shader.connectTextures();
        shader.stop();
    }



    public void render(GLEntity entity, EntityShader shader, ThreeDimensionCamera camera, List<Light> lights) {
        shader.start();
        shader.loadViewMatrix(camera.getViewMatrix());
        shader.loadLights(lights);
        shader.loadSkyColor(SKY_COLOR);
        prepareTexturedModel(entity.getModels().get(0), shader);
        shader.loadTransformationMatrix(Maths.createTransformationMatrix(entity.getPosition(),
                entity.getRx(), entity.getRy(), entity.getRz(), entity.getScale()));
        GL11.glDrawElements(GL11.GL_TRIANGLES, entity.getModels().get(0).getRawModel().getVertexCount(),
                    GL11.GL_UNSIGNED_INT, 0);
        unbindTexturedModel();
        shader.stop();
    }

    private void unbindTexturedModel() {
        MasterRenderer.enableCulling();
        GL20.glDisableVertexAttribArray(0);
        GL20.glDisableVertexAttribArray(1);
        GL20.glDisableVertexAttribArray(2);
        GL20.glDisableVertexAttribArray(3); //ani
        GL20.glDisableVertexAttribArray(4); //ani
        GL30.glBindVertexArray(0); //unbind vertex array

    }

    private static void bindTextures(ModelTexture modelTexture) {
        GL13.glActiveTexture(GL13.GL_TEXTURE0);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, modelTexture.getID());
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
    }

    private static void prepareTexturedModel(GLTexturedModel model, EntityShader shader) {
        GLRawModel rawModel = model.getRawModel();
        GL30.glBindVertexArray(rawModel.getVaoID());
        GL20.glEnableVertexAttribArray(0);
        GL20.glEnableVertexAttribArray(1);
        GL20.glEnableVertexAttribArray(2);
        ModelTexture texture = model.getTexture();
        shader.loadUseAnimation(model.isAnimated());
        shader.loadTextureAtlasNumberOfRows(texture.getNumberOfRows());
        if (texture.hasTransparency()) {
            MasterRenderer.disableCulling();
        }
        shader.loadFakeLightning(texture.isUseFakeLightning());
        shader.loadShineVariables(texture.getShineDamper(), texture.getReflectivity());
        GL13.glActiveTexture(GL13.GL_TEXTURE0);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, model.getTexture().getID());
        shader.loadUseSpecMap(texture.hasSpecularMap());
        if (texture.hasSpecularMap()) {
            GL13.glActiveTexture(GL13.GL_TEXTURE1);
            GL11.glBindTexture(GL11.GL_TEXTURE_2D, model.getTexture().getSpecularMapID());
        }
    }
}

