package engine;

import engine.graphics.Entity;
import engine.graphics.Scene;
import engine.graphics.animation.Animation;
import engine.graphics.animation.Animator;
import engine.graphics.glglfwImplementation.MasterRenderer;
import engine.graphics.glglfwImplementation.display.GLFWWindow;
import engine.graphics.glglfwImplementation.entities.GLEntity;
import engine.graphics.glglfwImplementation.guis.GuiTexture;
import engine.graphics.glglfwImplementation.models.GLMaterializedModel;
import engine.graphics.glglfwImplementation.text.GLGuiText;
import engine.graphics.glglfwImplementation.text.GLTTFont;
import engine.graphics.glglfwImplementation.textures.TerrainTexture;
import engine.graphics.glglfwImplementation.textures.TerrainTexturePack;
import engine.graphics.lights.PointLight;
import engine.graphics.materials.TexturedMaterial;
import engine.graphics.particles.ParticleMaster;
import engine.graphics.particles.ParticleSystem;
import engine.graphics.particles.ParticleSystemStream;
import engine.graphics.terrains.Terrain;
import engine.graphics.textures.TextureLoader;
import engine.inputs.InputEvent;
import engine.inputs.InputHandler;
import engine.inputs.listeners.InputEventListener;
import engine.toolbox.Color;
import engine.toolbox.Log;
import engine.toolbox.MeshCreator;
import engine.toolbox.OBJLoader;
import engine.toolbox.assimpLoader.AssimpMesh;
import engine.toolbox.assimpLoader.AssimpScene;
import org.joml.Vector2f;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

/***
 * Created by pv42 on 16.06.16.
 * @author pv42
 */

public class MainGameLoop {
    private static final String TAG = "GameLoop";
    private static final float FONT_SIZE = 0.00002f;

    public static void main(String[] args) {
        EngineMaster.init();
        GLFWWindow window = (GLFWWindow) EngineMaster.getDisplayManager().createWindow();
        InputHandler.addListener(new InputEventListener(InputEvent.MOUSE_EVENT, InputEvent.KEY_PRESS, InputEvent.L_MOUSE) {
            @Override
            public void onOccur(Vector2f v2f) {
                Log.i("EVENT_TESTER", "It works");
            }
        });
        window.setVSync(false);
        //todo AudioMaster.setListenerData();
        GLTTFont font = new GLTTFont("res/fonts/arial.ttf", 64);
        ParticleMaster.init(MasterRenderer.getProjectionMatrix());
        GLGuiText fpsText = new GLGuiText(font, "loading", FONT_SIZE, new Color(0.5f, 0.5f, 0.5f), new Vector2f(-1f, .96f));
        Scene scene = new Scene();
        scene.addText(fpsText);
        List<GLEntity> entities = new ArrayList<>();
        List<GuiTexture> guis = new ArrayList<>();
        TextureLoader tl = EngineMaster.getTextureLoader();
        GuiTexture gui = tl.loadGuiTexture("cross.png", new Vector2f(0f, 0f), new Vector2f(.04f, .04f), window);
        guis.add(gui);
        GLMaterializedModel texturedModel = new GLMaterializedModel(OBJLoader.loadObjModel("lowPolyTree"), new TexturedMaterial(tl.loadTexture("lowPolyTree.png")));
        texturedModel.getMaterial().setReflectivity(.1f);
        GLMaterializedModel texturedModel2 = new GLMaterializedModel(OBJLoader.loadObjModel("fern"), new TexturedMaterial(tl.loadTexture("fern.png")));
        // todo texturedModel2.getTexture().setHasTransparency(true);
        // todo texturedModel2.getTexture().setUseFakeLightning(true);
        PointLight sun = new PointLight(new Vector3f(500, 50000, 500), new Color(1, 1, .9), new Vector3f(1f, 0.00f, 0.00f));
        List<PointLight> lights = new ArrayList<>();
        lights.add(sun);
        //terrain textures
        TerrainTexture bgT = tl.loadTerrainTexture("grass.png");
        TerrainTexture rT = tl.loadTerrainTexture("dirt.png");
        TerrainTexture gT = tl.loadTerrainTexture("path.png");
        TerrainTexture bT = tl.loadTerrainTexture("grassFlowers.png");//grassFlowers
        TerrainTexturePack texturePack = new TerrainTexturePack(bgT, rT, bT, gT);
        TerrainTexture blendMap = tl.loadTerrainTexture("blendMap.png");
        Terrain terrain = new Terrain(0, 0, texturePack, blendMap, null);
        ParticleSystem particleSystem = new ParticleSystemStream(tl.loadParticleTexture("fire.png", false, 4, true, true), 10, 2.0f, 3f, new Vector3f(20, 10, 25), new Vector3f(5f, 5f, 5f));
        Random random = new Random();
        for (int i = 0; i < 500; i++) {
            float x = random.nextFloat() * 800;
            float z = random.nextFloat() * 800;
            entities.add(new GLEntity(texturedModel2, new Vector3f(x, terrain.getHeightOfTerrain(x, z), z)));
        }
        for (int i = 0; i < 300; i++) {
            float x = random.nextFloat() * 800;
            float z = random.nextFloat() * 800;
            GLEntity e = new GLEntity(texturedModel, new Vector3f(x, terrain.getHeightOfTerrain(x, z), z));
            e.setScale(.8f);
            entities.add(e);
        }
        //player
        AssimpScene assimpScene = new AssimpScene();
        assimpScene.load("C:\\Users\\pv42\\Documents\\IdeaProjects\\ThornsGameEngine\\testres\\cowboy.dae");
        List<GLMaterializedModel> cowboy = assimpScene.getMeshes().stream().map(AssimpMesh::createMaterializedModel).collect(Collectors.toList());
        Animation cowboyAnimation = assimpScene.getAnimations().get(0).getAnimation();
        Animator.applyAnimation(cowboyAnimation, cowboy.get(0).getRawModel().getJoints(), 0);
        Entity girl = new GLEntity(cowboy, new Vector3f(30, 0, 50));
        girl.setRx(-90);
        girl.setScale(5f);
        FirstPersonPlayer player = new FirstPersonPlayer(cowboy, new Vector3f(0, 0, 0), window);
        player.setScale(.8f);
        Entity cube = new GLEntity(new GLMaterializedModel(MeshCreator.createBox(1,1,1), new TexturedMaterial(tl.loadTexture("white.png"))), new Vector3f());
        ThirdPersonCamera camera = new ThirdPersonCamera(player);
        float timeSinceFPSUpdate = 0f;
        int framesSinceFPSUpdate = 0;

        Log.i(TAG, "starting render");
        float animationTime = 0;

        scene.addEntity(girl);
        scene.addTerrain(terrain);
        scene.addEntity(cube);
        lights.forEach(scene::addLight);
        guis.forEach(scene::addGui);
        entities.forEach(scene::addEntity);

        scene.addAniEntity(player);
        while (!window.isCloseRequested()) { //actual MainGameLoop
            //FPS Updates
            if (timeSinceFPSUpdate >= 1.7f) {
                fpsText.setString((int) (framesSinceFPSUpdate / timeSinceFPSUpdate) + "fps");
                timeSinceFPSUpdate = 0;
                framesSinceFPSUpdate = 0;
            }
            //girl.getModels().get(0).getRawModel().getJoints().get(10).rotate(0.0f,0.03f,0);
            player.move(terrain, window.getLastFrameTime());
            camera.move();
            particleSystem.generateParticles(new Vector3f(player.getEyePosition()), window.getLastFrameTime());
            ParticleMaster.update(window.getLastFrameTime());
            //animation
            animationTime += window.getLastFrameTime() * player.getCurrentSpeed() * 0.035f;
            if(animationTime < 0) animationTime += cowboyAnimation.getLastKeyFrameTime();
            animationTime %= cowboyAnimation.getLastKeyFrameTime();
            Animator.applyAnimation(cowboyAnimation, cowboy.get(0).getRawModel().getJoints(), animationTime);
            //game render
            MasterRenderer.render(scene, camera);
            window.update();
            //post render
            timeSinceFPSUpdate += window.getLastFrameTime();
            framesSinceFPSUpdate++;
        }
        EngineMaster.finish();
    }
}