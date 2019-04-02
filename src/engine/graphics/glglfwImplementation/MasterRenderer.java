package engine.graphics.glglfwImplementation;

import engine.graphics.Scene;
import engine.graphics.cameras.Camera;
import engine.graphics.display.Window;
import engine.graphics.glglfwImplementation.entities.GLEntityRenderer;
import engine.graphics.glglfwImplementation.guis.GuiRenderer;
import engine.graphics.glglfwImplementation.guis.GuiShader;
import engine.graphics.glglfwImplementation.lines.LineRenderer;
import engine.graphics.glglfwImplementation.text.GLGuiText;
import engine.graphics.glglfwImplementation.text.GLTextRenderer;
import engine.graphics.particles.ParticleMaster;
import engine.graphics.skybox.SkyboxRenderer;
import engine.graphics.terrains.TerrainRenderer;
import engine.graphics.terrains.TerrainShader;
import engine.graphics.text.GuiText;
import engine.toolbox.Log;
import engine.toolbox.Settings;
import engine.toolbox.Time;
import org.joml.Matrix4f;
import org.lwjgl.opengl.GL11;

import static engine.toolbox.Settings.SKY_COLOR;

public class MasterRenderer {
    //default
    private static final String TAG = "Engine:MasterRenderer";
    private static boolean enableSkybox = true;
    private static boolean use2D;
    private static Matrix4f projectionMatrix;
    //renderers
    private static GLEntityRenderer entityRenderer;
    private static GLEntityRenderer aniRenderer;
    private static TerrainRenderer terrainRenderer;
    private static TerrainShader terrainShader;
    private static SkyboxRenderer skyboxRenderer;
    private static LineRenderer lineRenderer;
    private static GuiRenderer guiRenderer;
    private static GuiShader guiShader;
    private static GLTextRenderer textRenderer;

    private static Window window;
    //performance calc
    private static long startT, preT, entT, normT, terT, skyT, partT, guiT, endT;

    /**
     * initializes the MasterRender
     *
     * @param use2D determents if the renders scene is in 2d or 3d
     */
    public static void init(boolean use2D) {
        MasterRenderer.use2D = use2D;
        Log.i(TAG, "initialised");
    }

    /**
     * initializes the MasterRenderer
     */
    public static void init() {
        enableCulling();
        createProjectionMatrix(getAspectRatio(), use2D);
        textRenderer = new GLTextRenderer();

        Log.i(TAG, " initialised");
    }

    public static void setWindow(Window window) {
        MasterRenderer.window = window;
        enableCulling();
        textRenderer = new GLTextRenderer();
        createProjectionMatrix(use2D);
        entityRenderer = new GLEntityRenderer(projectionMatrix);
        aniRenderer = new GLEntityRenderer(projectionMatrix);
        terrainShader = new TerrainShader();
        terrainRenderer = new TerrainRenderer(terrainShader, projectionMatrix);
        skyboxRenderer = new SkyboxRenderer(projectionMatrix, "stars");
        lineRenderer = new LineRenderer(projectionMatrix);
        guiShader = new GuiShader();
        guiRenderer = new GuiRenderer(getAspectRatio()); // todo move shader
    }

    /**
     * Updates the zoom
     *
     * @param zoom factor to zoom (1 is default)
     */
    public static void updateZoom(float zoom) {
        createProjectionMatrix(zoom, use2D);
        entityRenderer.updateProjectionMatrix(projectionMatrix);
        aniRenderer.updateProjectionMatrix(projectionMatrix);
        terrainRenderer.updateProjectionMatrix(projectionMatrix);
        lineRenderer.updateProjectionMatrix(projectionMatrix);
        skyboxRenderer.updateProjectionMatrix(projectionMatrix);
    }

    /**
     * renders a scene from the view of a camera in the current active window
     *
     * @param camera camera to use for render
     * @param scene  to render
     */
    public static void render(Scene scene, Camera camera) {
        startT = Time.getNanoTime();
        prepare();
        preT = Time.getNanoTime();
        //entities
        entityRenderer.render(scene.getEntities(), scene.getLights(), camera);
        //animation
        aniRenderer.render(scene.getAniEntities(), scene.getLights(), camera);
        entT = Time.getNanoTime();
        //terrain
        terrainRenderer.render(scene.getTerrains(), camera, scene.getLights());
        terT = Time.getNanoTime();
        //skybox
        if (enableSkybox) skyboxRenderer.render(camera, SKY_COLOR, window.getLastFrameTime());
        skyT = Time.getNanoTime();
        //particles
        ParticleMaster.renderParticles(camera);
        partT = Time.getNanoTime();
        //lines
        lineRenderer.render(scene.getLineStripModels(), camera);
        //gui
        guiRenderer.render(scene.getGuis());
        //text
        for (GuiText text : scene.getTexts()) {
            if(!(text instanceof GLGuiText)) {
                throw new UnsupportedOperationException("can't render none-GL fonts");
            }
            textRenderer.renderText((GLGuiText) text, getAspectRatio());
        }
        guiT = Time.getNanoTime();
        endT = Time.getNanoTime();
        double comT = 0.01 * (endT - startT);
        // System.out.println(String.format("pre:%.2f%% ent:%.2f%% nen:%.2f%% ter:%.2f%% sky:%.2f%% other:%.2f%%" ,(preT - startT) / comT,(entT - preT)/ comT,(normT - entT)/comT,(terT- normT)/comT,(skyT - terT)/comT,(endT-skyT)/comT  )  );
    }

    /**
     * enables / disables the skybox
     *
     * @param enable state to set skybox enable
     */
    public static void enableSkybox(boolean enable) {
        enableSkybox = enable;
    }


    public static void cleanUp() {
        entityRenderer.cleanUp();
        aniRenderer.cleanUp();
        terrainShader.cleanUp();
        guiShader.cleanUp();
        textRenderer.cleanUp();
        skyboxRenderer.cleanUp();
        lineRenderer.cleanUp();
    }

    private static void prepare() {
        //todo depth buffer seems not to work as intended GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
        GL11.glClearColor(SKY_COLOR.getR(), SKY_COLOR.getG(), SKY_COLOR.getB(), 1);
    }

    /**
     * creates the projectionMatrix
     *
     * @param zoom zooms factor
     */
    private static void createProjectionMatrix(float zoom, boolean use2D) {
        createProjectionMatrix(getAspectRatio(), zoom, use2D);
    }

    /**
     * creates the projectionMatrix
     */
    private static void createProjectionMatrix(boolean use2D) {
        createProjectionMatrix(getAspectRatio(), 1, use2D);
    }

    /**
     * creates the projectionMatrix
     *
     * @param zoom        zooms factor
     * @param aspectRatio windows aspect ratio
     */
    private static void createProjectionMatrix(float aspectRatio, float zoom, boolean use2D) {
        float y_scale = (float) ((1f / Math.tan(Math.toRadians(Settings.FOV / zoom / 2f))));
        float x_scale = y_scale / aspectRatio;
        float frustum_length = Settings.FAR_PLANE - Settings.NEAR_PLANE;
        projectionMatrix = new Matrix4f();
        projectionMatrix.m00(x_scale);
        projectionMatrix.m11(y_scale);
        if (use2D) return;
        projectionMatrix.m22(-((Settings.FAR_PLANE + Settings.NEAR_PLANE) / frustum_length));
        projectionMatrix.m23(-1);
        projectionMatrix.m32(-((2 * Settings.NEAR_PLANE * Settings.FAR_PLANE) / frustum_length));
        projectionMatrix.m33(0);
    }

    public static Matrix4f getProjectionMatrix() {
        return projectionMatrix;
    }

    /**
     * enables culling
     */
    public static void enableCulling() {
        GL11.glEnable(GL11.GL_CULL_FACE);
        GL11.glCullFace(GL11.GL_BACK);
    }

    /**
     * disables culling
     */
    public static void disableCulling() {
        GL11.glDisable(GL11.GL_CULL_FACE);
    }


    /**
     * calculates the windows current aspect ratio
     *
     * @return the current aspect ratio
     */
    public static float getAspectRatio() {
        return MasterRenderer.window.getAspectRatio();
    }

    public static void setAmbientLight(float ambientLight) {
        entityRenderer.setAmbientLight(ambientLight);
    }

}