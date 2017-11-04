package engineTester;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import engine.EngineMaster;
import engine.graphics.animation.Animation;
import engine.graphics.animation.Animator;
import engine.graphics.display.DisplayManager;
import engine.inputs.*;
import engine.inputs.listeners.InputEventListener;
import engine.toolbox.Color;
import engine.toolbox.collada.Collada;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import engine.graphics.cameras.FirstPersonCamera;
import engineTester.client.GameClient;
import engineTester.client.NetworkSender;
import engine.toolbox.collada.ColladaLoader;
import engine.graphics.entities.Entity;
import engine.graphics.entities.FirstPersonPlayer;
import engine.graphics.lights.Light;
import engine.graphics.fontMeshCreator.FontType;
import engine.graphics.fontMeshCreator.GUIText;
import engine.graphics.guis.GuiTexture;
import engine.toolbox.OBJLoader;
import engine.graphics.models.TexturedModel;
import engine.graphics.particles.*;
import engine.graphics.renderEngine.*;
import engine.graphics.terrains.Terrain;
import engine.graphics.textures.ModelTexture;
import engine.graphics.textures.TerrainTexture;
import engine.graphics.textures.TerrainTexturePack;
import engine.toolbox.Settings;
import engine.toolbox.Log;
import shivt.guns.*;

/***
 * Created by pv42 on 16.06.16.
 */

public class MainGameLoop {
    private static final String FONT = "courier_df";
    private static final String TAG = "GameLoop";
    private static final float FONT_SIZE = 1;
    private static boolean onlineMode = false;
    public static void main(String args[]) throws InterruptedException {
        EngineMaster.init();
        GameClient client = null;
        if(onlineMode) client = new GameClient();
        if(onlineMode) onlineMode = client.connect();
        if(onlineMode) onlineMode = client.login(Settings.SERVER_USERNAME, Settings.SERVER_PASSWORD);
        InputHandler.addListener(new InputEventListener(InputEvent.MOUSE_EVENT,InputEvent.KEY_PRESS,InputEvent.L_MOUSE) {
            @Override
            public void onOccur() {
                Log.i("EVENT_TESTER","It works");
            }
        });
        //todo AudioMaster.setListenerData();
        // objs
        
        FontType font = Loader.loadFont(FONT);
        ParticleMaster.init(MasterRenderer.getProjectionMatrix());
        GUIText text = new GUIText("loading",FONT_SIZE,font, new Vector2f(0,0),1,false);
        text.setColor(0.3f,0.3f,0.4f);
        MasterRenderer.loadText(text);
        List<Entity> entities = new ArrayList<>();
        List<GuiTexture> guis = new ArrayList<>();
        GuiTexture gui = new GuiTexture(Loader.loadTexture("cross.png"),new Vector2f(0f,0f),new Vector2f(.04f,.04f));
        guis.add(gui);
        TexturedModel texturedModel = new TexturedModel(OBJLoader.loadObjModel("lowPolyTree"),new ModelTexture(Loader.loadTexture("lowPolyTree.png")));
        texturedModel.getTexture().setReflectivity(.1f);
        TexturedModel texturedModel2 = new TexturedModel(OBJLoader.loadObjModel("fern"), new ModelTexture(Loader.loadTexture("fern.png")));
        texturedModel2.getTexture().setHasTransparency(true);
        texturedModel2.getTexture().setUseFakeLightning(true);
        Light sun = new Light(new Vector3f(500,50000,500), new Color(1,1,.9), new Vector3f(1f,0.00f, 0.00f));
        List<Light> lights = new ArrayList<>();
        lights.add(sun);
        //terrain engine.graphics.textures
        TerrainTexture bgT = new TerrainTexture(Loader.loadTexture("grass.png"));
        TerrainTexture rT = new TerrainTexture(Loader.loadTexture("dirt.png"));
        TerrainTexture gT = new TerrainTexture(Loader.loadTexture("path.png"));
        TerrainTexture bT = new TerrainTexture(Loader.loadTexture("grassFlowers.png"));//grassFlowers
        TerrainTexturePack texturePack = new TerrainTexturePack(bgT,rT,bT,gT);
        TerrainTexture blendMap = new TerrainTexture(Loader.loadTexture("blendMap.png"));
        Terrain terrain = new Terrain(0,0,texturePack,blendMap,null);
        ParticleSystem particleSystem = new ParticleSystemStream(new ParticleTexture(Loader.loadTexture("fire.png", false),4,true,true),1,1.3f,3f,new Vector3f(20,10,25),new Vector3f(2f,2f,2f));
        Random random = new Random();
        for(int i = 0; i<500; i++) {
            float x = random.nextFloat() * 800;
            float z = random.nextFloat() * 800;
            entities.add(new Entity(texturedModel2,new Vector3f(x,terrain.getHeightOfTerrain(x,z),z),0,0,0,1));
        }
        for(int i = 0; i<300; i++) {
            float x = random.nextFloat() * 800;
            float z = random.nextFloat() * 800;
            entities.add(new Entity(texturedModel,new Vector3f(x,terrain.getHeightOfTerrain(x,z),z),0,0,0,.8f));
        }
        //player
        Collada cowboyCollada = ColladaLoader.loadCollada("cowboy");
        List<TexturedModel> cowboy = cowboyCollada.getTexturedModels();
        Animation cowboyAnimation = cowboyCollada.getAnimation();
        Animator.applyAnimation(cowboyAnimation, cowboy.get(0).getRawModel().getJoints(), 0);
        List<TexturedModel> personModel = ColladaLoader.loadCollada("Laptop").getTexturedModels();
        Entity girl = new Entity(cowboy, new Vector3f(30,20,50),-90,0,0,5f);
        FirstPersonPlayer player = new FirstPersonPlayer(personModel, new Vector3f(0,0,0),0,0,0,0.8f);
        player.setGun(new Beretta92());
        FirstPersonCamera camera = new FirstPersonCamera(player);
        float timeSinceFPSUpdate = 0f;
        int framesSinceFPSUpdate = 0;
        //network
        NetworkSender networkSender = null;
        if(onlineMode) {
            networkSender = new NetworkSender(player, client);
            //networkSender.start();
        }
        Log.i(TAG, "starting render");
        float animationTime = 0;
        while (!DisplayManager.isCloseRequested()) { //actual MainGameLoop
            //game logic
            //FPS Updates
            if(timeSinceFPSUpdate >= 1.7f ) {
                text = new GUIText((int)(framesSinceFPSUpdate / timeSinceFPSUpdate) + "fps", FONT_SIZE, font, new Vector2f(0f, 0f), 1, false);
                text.setColor(1.0f, 1.0f, 0.0f);
                MasterRenderer.loadText(text);
                timeSinceFPSUpdate = 0;
                framesSinceFPSUpdate = 0;
            }
            //girl.getModels().get(0).getRawModel().getJoints().get(10).rotate(0.0f,0.03f,0);
            MasterRenderer.processText(text);
            MasterRenderer.processEntity(girl);
            player.move(terrain);
            camera.move();
            particleSystem.generateParticles(new Vector3f(player.getEyePosition()));
            ParticleMaster.update();
            //animation
            animationTime += DisplayManager.getFrameTimeSeconds() * 0.2;
            animationTime %= 1;
            Animator.applyAnimation(cowboyAnimation, cowboy.get(0).getRawModel().getJoints(), animationTime);
            //player.getModels().get(0).getRawModel().getJoints().get(10).rotate(new Vector3f(0,1,0),1);
            //game render
            processFirstPersonPlayer(player);
            MasterRenderer.processTerrain(terrain);
            guis.forEach(MasterRenderer::processGui);
            entities.forEach(MasterRenderer::processEntity);
            lights.forEach(MasterRenderer::processLight);

            MasterRenderer.render(camera,new Vector4f(0, -1, 0, 100000));
            DisplayManager.updateDisplay();
            InputLoop.loopHandle();
            //post render
            timeSinceFPSUpdate += DisplayManager.getFrameTimeSeconds();
            framesSinceFPSUpdate ++;
        }
        if(onlineMode && networkSender != null && client != null) {
            networkSender.end();
            try {
                networkSender.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            client.close();
        }
        EngineMaster.finish();
    }
    private static void processFirstPersonPlayer(FirstPersonPlayer player) {
        MasterRenderer.processAniEntity(player);
        if(player.getGun().getScope() != null && player.getGun().getScopingProgress() == 1.0f && player.getGun().getReloadCooldown() == 0) {
            MasterRenderer.processGui(player.getGun().getScope());
            MasterRenderer.updateZoom(4);
        } else {
            //todo MasterRenderer.processMMEntity(player.getGun());
            MasterRenderer.updateZoom(1 + player.getGun().getScopingProgress() * .5f);
        }
    }
}