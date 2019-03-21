package engine.graphics.renderEngine;

import engine.graphics.cameras.Camera;
import engine.graphics.display.Window;
import engine.graphics.entities.Entity;
import engine.graphics.glglfwImplementation.entities.GLEntityRenderer;
import engine.graphics.glglfwImplementation.entities.GLEntity;
import engine.graphics.glglfwImplementation.text.GLGuiText;
import engine.graphics.glglfwImplementation.text.GLTextRenderer;
import engine.graphics.guis.GuiRenderer;
import engine.graphics.guis.GuiShader;
import engine.graphics.guis.GuiTexture;
import engine.graphics.lights.Light;
import engine.graphics.lines.LineModel;
import engine.graphics.lines.LineRenderer;
import engine.graphics.models.TexturedModel;
import engine.graphics.particles.ParticleMaster;
import engine.graphics.skybox.SkyboxRenderer;
import engine.graphics.terrains.Terrain;
import engine.graphics.terrains.TerrainShader;
import engine.toolbox.Log;
import engine.toolbox.Settings;
import engine.toolbox.Time;
import org.joml.Matrix4f;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    private static GuiShader guiShader = new GuiShader();
    private static GLTextRenderer textRenderer;
    //rendering objects
    private static Map<List<TexturedModel>, List<GLEntity>> entities = new HashMap<>();
    private static Map<List<TexturedModel>, List<GLEntity>> aniEntities = new HashMap<>();
    //private static Map<TexturedModel,List<GLEntity>> normalEntities = new HashMap<>();
    private static List<Terrain> terrains = new ArrayList<>();
    private static List<LineModel> lineStripModels = new ArrayList<>();
    private static List<GuiTexture> guis = new ArrayList<>();
    private static List<GLGuiText> texts = new ArrayList<>();
    private static List<Light> lights;
    //
    private static Window window;
    //performance calc
    private static long startT, preT, entT, normT, terT, skyT, partT, guiT, endT;

    /**
     * initializes the MasterRender
     */
    public static void init(Window window, boolean use2D) {
        MasterRenderer.window = window;
        enableCulling();
        MasterRenderer.use2D = use2D;
        createProjectionMatrix(use2D);
        entityRenderer = new GLEntityRenderer(projectionMatrix);
        aniRenderer = new GLEntityRenderer(projectionMatrix);
        terrainShader = new TerrainShader();
        terrainRenderer = new TerrainRenderer(terrainShader, projectionMatrix);
        skyboxRenderer = new SkyboxRenderer(projectionMatrix, "stars");
        lineRenderer = new LineRenderer(projectionMatrix);
        guiRenderer = new GuiRenderer(getAspectRatio()); // todo
        textRenderer = new GLTextRenderer();
        lights = new ArrayList<>();
        Log.i(TAG, "initialised");
    }

    /**
     * initializes the MasterRenderer with specific aspect ratio
     *
     * @param window to render in
     */
    public static void init(Window window) {
        MasterRenderer.window = window;
        enableCulling();
        createProjectionMatrix(getAspectRatio(), use2D);
        entityRenderer = new GLEntityRenderer(projectionMatrix);
        aniRenderer = new GLEntityRenderer(projectionMatrix);
        terrainShader = new TerrainShader();
        terrainRenderer = new TerrainRenderer(terrainShader, projectionMatrix);
        skyboxRenderer = new SkyboxRenderer(projectionMatrix, "stars");
        lineRenderer = new LineRenderer(projectionMatrix);
        guiRenderer = new GuiRenderer(getAspectRatio()); // todo
        textRenderer = new GLTextRenderer();

        Log.i(TAG, " initialised");
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
     * Renders the scene
     *
     * @param camera camera to use for render
     */
    public static void render(Camera camera) {
        startT = Time.getNanoTime();
        prepare();
        preT = Time.getNanoTime();
        //entities
        entityRenderer.render(entities, lights, camera);
        //animation
        aniRenderer.render(aniEntities, lights, camera);
        entT = Time.getNanoTime();
        //normal
        //todo normalRenderer.render(normalEntities,clipPlane,lights, camera);
        normT = Time.getNanoTime();
        //terrain
        terrainRenderer.render(terrains, camera, lights);
        terT = Time.getNanoTime();
        //skybox
        if (enableSkybox) skyboxRenderer.render(camera, SKY_COLOR, window.getLastFrameTime());
        skyT = Time.getNanoTime();
        //particles
        ParticleMaster.renderParticles(camera);
        partT = Time.getNanoTime();
        //lines
        lineRenderer.render(lineStripModels, camera);
        //gui
        guiRenderer.render(guis);
        //text
        for (GLGuiText text: texts) {
            textRenderer.renderText(text, getAspectRatio());
        }
        guiT = Time.getNanoTime();
        endT = Time.getNanoTime();
        double comT = 0.01 * (endT - startT);

        //System.out.println(String.format("pre:%.2f%% ent:%.2f%% nen:%.2f%% ter:%.2f%% sky:%.2f%% other:%.2f%%" ,(preT - startT) / comT,(entT - preT)/ comT,(normT - entT)/comT,(terT- normT)/comT,(skyT - terT)/comT,(endT-skyT)/comT  )  );
    }

    /**
     * enables / disables the skybox
     *
     * @param enable state to set skybox enable
     */
    public static void enableSkybox(boolean enable) {
        enableSkybox = enable;
    }

    public static void addTerrain(Terrain terrain) {
        terrains.add(terrain);
    }

    public static void addEntity(Entity entity) {
        if (!(entity instanceof GLEntity)) {
            throw new UnsupportedOperationException("Can't process none GL entities");
        }
        GLEntity glEntity = (GLEntity) entity;
        List<TexturedModel> entityModels = glEntity.getModels();
        if (true) {
            List<GLEntity> batch = entities.get(entityModels);
            if (batch != null) {
                batch.add(glEntity);
            } else {
                List<GLEntity> newBatch = new ArrayList<>();
                newBatch.add(glEntity);
                entities.put(entityModels, newBatch);
            }
        } else {
            //todo normal entities
            /*
            List<GLEntity> batch = normalEntities.get(entityModel);
            if(batch!=null){

                batch.add(entity);
            }else{
                List<GLEntity> newBatch = new ArrayList<>();
                newBatch.add(entity);
                normalEntities.put(entityModel, newBatch);
            }*/
        }
    }

    public static void addAniEntity(GLEntity entity) {
        List<TexturedModel> entityModel = entity.getModels(); //// TODO: 10.08.16 ?
        List<GLEntity> batch = aniEntities.get(entityModel);
        if (batch != null) {
            batch.add(entity);
        } else {
            List<GLEntity> newBatch = new ArrayList<>();
            newBatch.add(entity);
            aniEntities.put(entityModel, newBatch);
        }
    }

    public static void addLine(LineModel lineStripModel) {
        lineStripModels.add(lineStripModel);
    }

    public static void addGui(GuiTexture gui) {
        guis.add(gui);
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

    public static void addText(GLGuiText text) {
        texts.add(text);
    }


    public static void addLight(Light light) {
        lights.add(light);
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

    public void clearAll() {
        guis.clear();
        terrains.clear();
        entities.clear();
        aniEntities.clear();
        lineStripModels.clear();
        //todo normalEntities.clear();
        texts.clear();
        lights.clear();
    }
}