package engine.graphics.renderEngine;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import engine.graphics.cameras.Camera;
import engine.graphics.display.DisplayManager;
import engine.graphics.fontMeshCreator.FontType;
import engine.graphics.fontMeshCreator.GUIText;
import engine.graphics.fontMeshCreator.TextMeshData;
import engine.graphics.fontRendering.FontRenderer;
import engine.graphics.guis.GuiRenderer;
import engine.graphics.guis.GuiShader;
import engine.graphics.guis.GuiTexture;
import engine.graphics.lines.LineRenderer;
import engine.graphics.lines.LineModel;
import engine.graphics.models.TexturedModel;
import engine.graphics.normalMappingRenderer.NormalMappingRenderer;
import engine.graphics.particles.ParticleMaster;
import engine.graphics.terrains.TerrainShader;
import engine.graphics.skybox.SkyboxRenderer;
import engine.graphics.terrains.Terrain;
import engine.graphics.cameras.ThreeDimensionCamera;
import engine.graphics.entities.Entity;
import engine.graphics.lights.Light;

import engine.toolbox.Log;
import engine.toolbox.Settings;
import engine.toolbox.Time;

import org.lwjgl.opengl.*;
import org.joml.Matrix4f;
import org.joml.Vector4f;

import static engine.toolbox.Settings.SKY_COLOR;

public class MasterRenderer {
    private static boolean enableSkybox = true;
    private static boolean use2D;
    //default
    private static final String TAG = "Engine:MasterRenderer";
    private static Matrix4f projectionMatrix;
    //renderers
    private static EntityRenderer entityRenderer;
    private static EntityRenderer aniRenderer;
    private static NormalMappingRenderer normalRenderer;
    private static TerrainRenderer terrainRenderer;
    private static TerrainShader terrainShader = new TerrainShader();
    private static SkyboxRenderer skyboxRenderer;
    private static LineRenderer lineRenderer;
    private static GuiRenderer guiRenderer;
    private static GuiShader guiShader = new GuiShader();
    private static FontRenderer fontRenderer;
    //rendering objects
    private static Map<List<TexturedModel>, List<Entity>> entities = new HashMap<>();
    private static Map<List<TexturedModel>, List<Entity>> aniEntities = new HashMap<>();
    //private static Map<TexturedModel,List<Entity>> normalEntities = new HashMap<>();
    private static List<Terrain> terrains = new ArrayList<>();
    private static List<LineModel> lineStripModels = new ArrayList<>();
    private static List<GuiTexture> guis = new ArrayList<>();
    private static Map<FontType, List<GUIText>> texts = new HashMap<>();
    private static List<Light> lights;
    //performance calc
    private static long startT, preT, entT, normT, terT, skyT, partT, guiT, endT;

    /**
     * initializes the MasterRender
     */
    public static void init( boolean use2D) {
        enableCulling();
        MasterRenderer.use2D = use2D;
        createProjectionMatrix(use2D);
        entityRenderer = new EntityRenderer(projectionMatrix);
        aniRenderer = new EntityRenderer(projectionMatrix);
        normalRenderer = new NormalMappingRenderer(projectionMatrix);
        terrainRenderer = new TerrainRenderer(terrainShader, projectionMatrix);
        skyboxRenderer = new SkyboxRenderer(projectionMatrix, "stars");
        lineRenderer = new LineRenderer(projectionMatrix);
        guiRenderer = new GuiRenderer(getAspectRatio()); // todo
        fontRenderer = new FontRenderer(getAspectRatio());
        lights = new ArrayList<>();
        Log.i(TAG, "initialised");
    }

    /**
     * initializes the MasterRenderer with specific aspect ratio
     *
     * @param aspectRatio specific aspect ratio
     */
    public static void init(float aspectRatio) {
        enableCulling();
        createProjectionMatrix(aspectRatio,use2D);
        entityRenderer = new EntityRenderer(projectionMatrix);
        aniRenderer = new EntityRenderer(projectionMatrix);
        normalRenderer = new NormalMappingRenderer(projectionMatrix);
        terrainRenderer = new TerrainRenderer(terrainShader, projectionMatrix);
        skyboxRenderer = new SkyboxRenderer(projectionMatrix, "stars");
        lineRenderer = new LineRenderer(projectionMatrix);
        guiRenderer = new GuiRenderer(aspectRatio); // todo
        fontRenderer = new FontRenderer(aspectRatio);
        Log.i(TAG, " initialised");
    }

    /**
     * Updates the zoom
     *
     * @param zoom factor to zoom (1 is default)
     */
    public static void updateZoom(float zoom) {
        createProjectionMatrix(zoom,use2D);
        entityRenderer.updateProjectionMatrix(projectionMatrix);
        aniRenderer.updateProjectionMatrix(projectionMatrix);
        normalRenderer.updateProjectionMatrix(projectionMatrix);
        terrainRenderer.updateProjectionMatrix(projectionMatrix);
        lineRenderer.updateProjectionMatrix(projectionMatrix);
        skyboxRenderer.updateProjectionMatrix(projectionMatrix);
    }

    /**
     * Renders the scene
     *
     * @param camera    camera to use for render
     * @param clipPlane clipPlane for Water shading
     */
    public static void render(Camera camera, Vector4f clipPlane) {
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
        if(enableSkybox) skyboxRenderer.render(camera, SKY_COLOR);
        skyT = Time.getNanoTime();
        //particles
        ParticleMaster.renderParticles(camera);
        partT = Time.getNanoTime();
        //lines
        lineRenderer.render(lineStripModels, camera);
        //gui
        guiRenderer.render(guis);
        //text
        fontRenderer.render(texts, camera, projectionMatrix);
        guiT = Time.getNanoTime();
        texts.clear();
        endT = Time.getNanoTime();
        double comT = 0.01 * (endT - startT);

        //System.out.println(String.format("pre:%.2f%% ent:%.2f%% nen:%.2f%% ter:%.2f%% sky:%.2f%% other:%.2f%%" ,(preT - startT) / comT,(entT - preT)/ comT,(normT - entT)/comT,(terT- normT)/comT,(skyT - terT)/comT,(endT-skyT)/comT  )  );
    }

    public void clearAll() {
        guis.clear();
        terrains.clear();
        entities.clear();
        aniEntities.clear();
        lineStripModels.clear();
        //todo normalEntities.clear();
        lights.clear();
    }

    /**
     * enables / disables the skybox
     * @param enable state to set skybox enable
     */
    public static void enableSkybox(boolean enable) {
        enableSkybox = enable;
    }


    public static void addTerrain(Terrain terrain) {
        terrains.add(terrain);
    }

    public static void addEntity(Entity entity) {
        List<TexturedModel> entityModels = entity.getModels();
        if (true) {
            List<Entity> batch = entities.get(entityModels);
            if (batch != null) {
                batch.add(entity);
            } else {
                List<Entity> newBatch = new ArrayList<>();
                newBatch.add(entity);
                entities.put(entityModels, newBatch);
            }
        } else {
            //todo normal entities
            /*
            List<Entity> batch = normalEntities.get(entityModel);
            if(batch!=null){

                batch.add(entity);
            }else{
                List<Entity> newBatch = new ArrayList<>();
                newBatch.add(entity);
                normalEntities.put(entityModel, newBatch);
            }*/
        }
    }

    public static void addAniEntity(Entity entity) {
        List<TexturedModel> entityModel = entity.getModels(); //// TODO: 10.08.16 ?
        List<Entity> batch = aniEntities.get(entityModel);
        if (batch != null) {
            batch.add(entity);
        } else {
            List<Entity> newBatch = new ArrayList<>();
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
        fontRenderer.cleanUp();
        normalRenderer.cleanUp();
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
        if(use2D) return;
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

    public static void loadText(GUIText text) {
        FontType font = text.getFont();
        TextMeshData data = font.loadText(text);
        int vao = Loader.loadToVAO(data.getVertexPositions(), data.getTextureCoords());
        text.setMeshInfo(vao, data.getVertexCount());
    }

    public static void processText(GUIText text) {
        FontType font = text.getFont();
        List<GUIText> textBatch = texts.computeIfAbsent(font, k -> new ArrayList<>());
        textBatch.add(text);
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
        return DisplayManager.getSize().x() / DisplayManager.getSize().y();
    }

    public static void setAmbientLight(float ambientLight) {
        entityRenderer.setAmbientLight(ambientLight);
    }
}