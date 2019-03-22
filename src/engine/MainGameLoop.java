package engine;

import engine.graphics.Scene;
import engine.graphics.animation.Animation;
import engine.graphics.animation.Animator;
import engine.graphics.cameras.FirstPersonCamera;
import engine.graphics.entities.Entity;
import engine.graphics.glglfwImplementation.GLLoader;
import engine.graphics.glglfwImplementation.display.GLFWWindow;
import engine.graphics.glglfwImplementation.entities.FirstPersonPlayer;
import engine.graphics.glglfwImplementation.entities.GLEntity;
import engine.graphics.glglfwImplementation.models.GLTexturedModel;
import engine.graphics.glglfwImplementation.text.GLGuiText;
import engine.graphics.glglfwImplementation.text.GLTTFont;
import engine.graphics.guis.GuiTexture;
import engine.graphics.lights.Light;
import engine.graphics.particles.ParticleMaster;
import engine.graphics.particles.ParticleSystem;
import engine.graphics.particles.ParticleSystemStream;
import engine.graphics.particles.ParticleTexture;
import engine.graphics.glglfwImplementation.MasterRenderer;
import engine.graphics.terrains.Terrain;
import engine.graphics.glglfwImplementation.textures.ModelTexture;
import engine.graphics.glglfwImplementation.textures.TerrainTexture;
import engine.graphics.glglfwImplementation.textures.TerrainTexturePack;
import engine.inputs.InputEvent;
import engine.inputs.InputHandler;
import engine.inputs.listeners.InputEventListener;
import engine.toolbox.Color;
import engine.toolbox.Log;
import engine.toolbox.OBJLoader;
import engine.toolbox.collada.Collada;
import engine.toolbox.collada.ColladaLoader;
import org.joml.Vector2f;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/***
 * Created by pv42 on 16.06.16.
 */

public class MainGameLoop {
    private static final String FONT = "courier_df";
    private static final String TAG = "GameLoop";
    private static final float FONT_SIZE = 0.00005f;

    public static void main(String[] args) {
        GLFWWindow window = EngineMaster.init();
        InputHandler.addListener(new InputEventListener(InputEvent.MOUSE_EVENT, InputEvent.KEY_PRESS, InputEvent.L_MOUSE) {
            @Override
            public void onOccur() {
                Log.i("EVENT_TESTER", "It works");
            }
        });
        window.setVSync(false);
        //todo AudioMaster.setListenerData();
        GLTTFont font = new GLTTFont("res/fonts/arial.ttf", 64);
        ParticleMaster.init(MasterRenderer.getProjectionMatrix());
        GLGuiText text = new GLGuiText(font, "loading", FONT_SIZE, new Color(0.3f, 0.3f, 0.4f), new Vector2f(0, 0));
        Scene scene = new Scene();
        scene.addText(text);
        List<GLEntity> entities = new ArrayList<>();
        List<GuiTexture> guis = new ArrayList<>();
        GuiTexture gui = new GuiTexture(GLLoader.loadTexture("cross.png"), new Vector2f(0f, 0f), new Vector2f(.04f, .04f), window);
        guis.add(gui);
        GLTexturedModel texturedModel = new GLTexturedModel(OBJLoader.loadObjModel("lowPolyTree"), new ModelTexture(GLLoader.loadTexture("lowPolyTree.png")));
        texturedModel.getTexture().setReflectivity(.1f);
        GLTexturedModel texturedModel2 = new GLTexturedModel(OBJLoader.loadObjModel("fern"), new ModelTexture(GLLoader.loadTexture("fern.png")));
        texturedModel2.getTexture().setHasTransparency(true);
        texturedModel2.getTexture().setUseFakeLightning(true);
        Light sun = new Light(new Vector3f(500, 50000, 500), new Color(1, 1, .9), new Vector3f(1f, 0.00f, 0.00f));
        List<Light> lights = new ArrayList<>();
        lights.add(sun);
        //terrain textures
        TerrainTexture bgT = new TerrainTexture(GLLoader.loadTexture("grass.png"));
        TerrainTexture rT = new TerrainTexture(GLLoader.loadTexture("dirt.png"));
        TerrainTexture gT = new TerrainTexture(GLLoader.loadTexture("path.png"));
        TerrainTexture bT = new TerrainTexture(GLLoader.loadTexture("grassFlowers.png"));//grassFlowers
        TerrainTexturePack texturePack = new TerrainTexturePack(bgT, rT, bT, gT);
        TerrainTexture blendMap = new TerrainTexture(GLLoader.loadTexture("blendMap.png"));
        Terrain terrain = new Terrain(0, 0, texturePack, blendMap, null);
        ParticleSystem particleSystem = new ParticleSystemStream(new ParticleTexture(GLLoader.loadTexture("fire.png", false), 4, true, true), 10, 2.0f, 3f, new Vector3f(20, 10, 25), new Vector3f(5f, 5f, 5f));
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
        Collada cowboyCollada = ColladaLoader.loadCollada("cowboy");
        List<GLTexturedModel> cowboy = cowboyCollada.getTexturedModels();
        Animation cowboyAnimation = cowboyCollada.getAnimation();
        Animator.applyAnimation(cowboyAnimation, cowboy.get(0).getRawModel().getJoints(), 0);
        //List<GLTexturedModel> personModel = ColladaLoader.loadCollada("Laptop").getTexturedModels();
        Entity girl = new GLEntity(cowboy, new Vector3f(30, 20, 50));
        girl.setRx(-90);
        girl.setScale(5f);
        FirstPersonPlayer player = new FirstPersonPlayer(cowboy, new Vector3f(0, 0, 0), window);
        player.setScale(.8f);
        Entity cube = new GLEntity(new GLTexturedModel(OBJLoader.loadObjModel("cube"), new ModelTexture(GLLoader.loadTexture("white.png"))), new Vector3f());
        FirstPersonCamera camera = new FirstPersonCamera(player);
        float timeSinceFPSUpdate = 0f;
        int framesSinceFPSUpdate = 0;
        //network

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
            //game logic
            //FPS Updates
            if (timeSinceFPSUpdate >= 1.7f) {
                text.setString((int) (framesSinceFPSUpdate / timeSinceFPSUpdate) + "fps");
                timeSinceFPSUpdate = 0;
                framesSinceFPSUpdate = 0;
            }
            //girl.getModels().get(0).getRawModel().getJoints().get(10).rotate(0.0f,0.03f,0);
            player.move(terrain, window.getLastFrameTime());
            camera.move();
            particleSystem.generateParticles(new Vector3f(player.getEyePosition()), window.getLastFrameTime());
            ParticleMaster.update(window.getLastFrameTime());
            //animation
            animationTime += window.getLastFrameTime() * 0.2;
            animationTime %= 1;
            Animator.applyAnimation(cowboyAnimation, cowboy.get(0).getRawModel().getJoints(), animationTime);
            //player.getModels().get(0).getRawModel().getJoints().get(10).rotate(new Vector3f(0,1,0),1);
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