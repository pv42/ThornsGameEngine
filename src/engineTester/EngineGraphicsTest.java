package engineTester;

import engine.graphics.display.DisplayManager;
import engine.graphics.cameras.StaticCamera;
import engine.graphics.entities.Entity;
import engine.graphics.models.OBJLoader;
import engine.graphics.models.RawModel;
import engine.graphics.models.TexturedModel;
import engine.graphics.renderEngine.Loader;
import engine.graphics.shaders.StaticShader;
import engine.graphics.textures.ModelTexture;
import engine.toolbox.Maths;
import engine.toolbox.Settings;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.BufferUtils;
import org.lwjgl.glfw.*;
import org.lwjgl.opengl.*;

import java.nio.ByteBuffer;

import static engine.toolbox.Settings.HEIGHT;
import static engine.toolbox.Settings.WIDTH;
import static java.lang.Math.floor;
import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.glfw.GLFW.glfwShowWindow;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL11.GL_QUADS;
import static org.lwjgl.opengl.GL11.glDrawArrays;
import static org.lwjgl.stb.STBEasyFont.stb_easy_font_print;
import static org.lwjgl.system.MemoryUtil.NULL;

/**
 * Created by pv42 on 11.07.2017.
 */
public class EngineGraphicsTest {
    private static Matrix4f projectionMatrix;
    private static int progress = 1;
    private static float time = 1;
    public static void main(String args[]) {
        //EngineMaster.init();
        //int texture = Loader.loadTexture("grass.png");
        //TexturedModel model = new TexturedModel(OBJLoader.loadObjModel("dragon"), new ModelTexture(texture));
        //Entity entity = new Entity(model,new Vector3f(0,0,0),0,0,0,1);
        //Camera camera = new StaticCamera(new Vector3f(-10,0,0), new Vector3f(0,0,0));
        long window;
        GLFWErrorCallback errorCallback = GLFWErrorCallback.createPrint().set();
        if (!glfwInit()) {
            throw new IllegalStateException("Unable to initialize GLFW");
        }
        glfwDefaultWindowHints();
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
        glfwWindowHint(GLFW_RESIZABLE, GLFW_FALSE);

        if(false) {
            window = glfwCreateWindow(WIDTH, HEIGHT, "H@", NULL, NULL);
            if (window == NULL) {
                throw new RuntimeException("Failed to create the GLFW window");
            }
            glfwMakeContextCurrent(window);
            GL.createCapabilities();
            glfwSwapInterval(1);
            glfwShowWindow(window);
            glOrtho(0.0, WIDTH, HEIGHT, 0.0, -1.0, 1.0);


        } else  {
            DisplayManager.init();
            window = DisplayManager.createWindow().getId();
        }
        //glMatrixMode(GL_PROJECTION);
        //glLoadIdentity();
        glMatrixMode(GL_MODELVIEW);

        ByteBuffer charBuffer = BufferUtils.createByteBuffer(256 * 270);

        glEnableClientState(GL_VERTEX_ARRAY);
        glVertexPointer(2, GL_FLOAT, 16, charBuffer);

        glClearColor(43f / 255f, 43f / 255f, 43f / 255f, 0f); // BG color
        RawModel rawModel = OBJLoader.loadObjModel("dragon");
        createProjectionMatrix(1,1);
        while (!glfwWindowShouldClose(window)) {
            glfwPollEvents();
            if(false) {

                glClear(GL_COLOR_BUFFER_BIT);

                // Progress bar
                glPushMatrix();
                glTranslatef(WIDTH * 0.5f, HEIGHT * 0.5f, 0.0f);
                glBegin(GL_QUADS);
                {
                    glColor3f(0.5f * 43f / 255f, 0.5f * 43f / 255f, 0.5f * 43f / 255f);
                    glVertex2f(-256.0f, -32.0f);
                    glVertex2f(256.0f, -32.0f);
                    glVertex2f(256.0f, 32.0f);
                    glVertex2f(-256.0f, 32.0f);

                    glColor3f(0.5f, 0.5f, 0.0f);

                    glVertex2f(-254.0f, -30.0f);
                    glVertex2f(-254.0f + progress * 508.0f, -30.0f);
                    glVertex2f(-254.0f + progress * 508.0f, 30.0f);
                    glVertex2f(-254.0f, 30.0f);
                }
                glEnd();
                glPopMatrix();

                glColor3f(169f / 255f, 183f / 255f, 198f / 255f); // Text color

                // Progress text
                int minutes = (int) floor(time / 60.0f);
                int seconds = (int) floor((time - minutes * 60.0f));
                int quads = stb_easy_font_print(WIDTH * 0.5f - 13, HEIGHT * 0.5f - 4, String.format("%02d:%02d", minutes, seconds), null, charBuffer);
                glDrawArrays(GL_QUADS, 0, quads * 4);

                // HUD
                quads = stb_easy_font_print(4, 4, "Press HOME to rewind", null, charBuffer);
                glDrawArrays(GL_QUADS, 0, quads * 4);

                quads = stb_easy_font_print(4, 20, "Press LEFT/RIGHT or LMB to seek", null, charBuffer);
                glDrawArrays(GL_QUADS, 0, quads * 4);

                quads = stb_easy_font_print(4, 36, "Press SPACE to pause/resume", null, charBuffer);
                glDrawArrays(GL_QUADS, 0, quads * 4);


            } else  {
                render(rawModel);
                StaticShader shader = new StaticShader();
                shader.start();
                shader.loadProjectionMatrix(projectionMatrix);
                shader.connectTextures();
                shader.stop();
                render(new Entity(new TexturedModel(rawModel,new ModelTexture(Loader.loadTexture("grass.png"))),new Vector3f(),0,0,0,0),shader);
            }
            glfwSwapBuffers(window);

        }
        glfwFreeCallbacks(window);
        glfwDestroyWindow(window);

        glfwTerminate();
        glfwSetErrorCallback(null).free();
    }

    private static void render(RawModel model) {
        GL30.glBindVertexArray(model.getVaoID());
        GL20.glEnableVertexAttribArray(0);
        GL11.glDrawElements(GL11.GL_TRIANGLES, model.getVertexCount(), GL11.GL_UNSIGNED_INT, 0);
        GL20.glDisableVertexAttribArray(0);
        GL30.glBindVertexArray(0);
    }
    public static void render(Entity entity, StaticShader shader) {
        TexturedModel model = entity.getModels().get(0);
        RawModel rawModel = model.getRawModel();
        GL30.glBindVertexArray(rawModel.getVaoID());
        GL20.glEnableVertexAttribArray(0);
        GL20.glEnableVertexAttribArray(1);
        GL20.glEnableVertexAttribArray(2);
        shader.loadViewMatrix(new StaticCamera(new Vector3f(10000000,-1529560,-1000000000), new Vector3f(10,10,80)));
        Matrix4f transformationMatrix = Maths.createTransformationMatrix(entity.getPosition(), entity.getRx(), entity.getRy(), entity.getRz(), entity.getScale());
        shader.loadTransformationMatrix(transformationMatrix);
        ModelTexture texture = model.getTexture();
        shader.loadShineVariables(texture.getShineDamper(), texture.getReflectivity());
        GL13.glActiveTexture(GL13.GL_TEXTURE1);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, model.getTexture().getID());
        GL11.glDrawElements(GL11.GL_TRIANGLES, rawModel.getVertexCount(), GL11.GL_UNSIGNED_INT, 0);
        GL20.glDisableVertexAttribArray(0);
        GL20.glDisableVertexAttribArray(1);
        GL20.glDisableVertexAttribArray(2);
        GL30.glBindVertexArray(0);
    }
    private static void createProjectionMatrix(float aspectRatio, float zoom) {
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
}

