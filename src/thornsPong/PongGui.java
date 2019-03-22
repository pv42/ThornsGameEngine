package thornsPong;

import engine.EngineMaster;
import engine.graphics.Scene;
import engine.graphics.cameras.TwoDimensionsCamera;
import engine.graphics.display.Window;
import engine.graphics.glglfwImplementation.models.GLTexturedModel;
import engine.graphics.glglfwImplementation.GLLoader;
import engine.graphics.glglfwImplementation.MasterRenderer;
import engine.graphics.glglfwImplementation.textures.ModelTexture;
import engine.inputs.InputHandler;
import engine.inputs.listeners.InputEventListener;
import engine.physics.CuboidHitBox;
import engine.physics.HitBox;
import engine.physics.PhysicalEntity;
import engine.physics.PhysicsEngine;
import engine.toolbox.MeshCreator;
import org.joml.Vector3f;
import org.lwjgl.glfw.GLFW;


import static engine.inputs.InputEvent.KEY_EVENT;
import static engine.inputs.InputEvent.KEY_PRESS;
import static engine.inputs.InputEvent.KEY_RELEASE;
import static engine.physics.PhysicsEngine.COLLISION_TYPE_INV_Y;
import static engine.physics.PhysicsEngine.COLLISION_TYPE_PONG;

public class PongGui {
    private boolean klu = false;
    private boolean kld = false;
    private boolean kru = false;
    private boolean krd = false;
    private boolean flipPause = false;
    private PongGame game;
    public PongGui() {
        Window window = EngineMaster.init(true);
        TwoDimensionsCamera camera = new TwoDimensionsCamera();
        ModelTexture texture = new ModelTexture(GLLoader.loadTexture("white.png"));
        //boundings
        GLTexturedModel boundingModel = new GLTexturedModel(MeshCreator.createBox(2,.2f,1), texture);
        CuboidHitBox boundingHitBox = new CuboidHitBox(-1,1,-.1f,.1f,-.5f,.5f);
        PhysicalEntity topBounding = new PhysicalEntity(boundingModel, new Vector3f(0,.7f,0), 1);
        topBounding.setStatic(true);
        topBounding.setHitBox(boundingHitBox);
        topBounding.setCollisionType(COLLISION_TYPE_INV_Y);
        PhysicsEngine.addPhysical(topBounding);
        PhysicalEntity bottomBounding = new PhysicalEntity(boundingModel, new Vector3f(0,-.7f,0),1);
        bottomBounding.setStatic(true);
        bottomBounding.setHitBox(boundingHitBox);
        bottomBounding.setCollisionType(COLLISION_TYPE_INV_Y);
        PhysicsEngine.addPhysical(bottomBounding);
        //paddles
        GLTexturedModel paddleModel = new GLTexturedModel(MeshCreator.createBox(.2f,.2f,.2f),texture);
        HitBox paddleHitBox = new CuboidHitBox(-.1f,.1f,-.1f,.1f,-.1f,.1f);
        //left paddle
        PhysicalEntity leftPaddle = new PhysicalEntity(paddleModel, new Vector3f(),1);
        leftPaddle.setStatic(true);
        leftPaddle.setCollisionType(COLLISION_TYPE_PONG);
        leftPaddle.setHitBox(paddleHitBox);
        PhysicsEngine.addPhysical(leftPaddle);
        //right paddle
        PhysicalEntity rightPaddle = new PhysicalEntity(paddleModel, new Vector3f(),1);
        rightPaddle.setStatic(true);
        rightPaddle.setCollisionType(COLLISION_TYPE_PONG);
        rightPaddle.setHitBox(paddleHitBox);
        PhysicsEngine.addPhysical(rightPaddle);
        //ball
        GLTexturedModel ballModel = new GLTexturedModel(MeshCreator.createCircle(.02f,24),texture);
        HitBox ballHitBox = new CuboidHitBox(-.01f,.01f,-.01f,.01f,-.01f,.01f);
        PhysicalEntity ball = new PhysicalEntity(ballModel,new Vector3f(),0);
        ball.setHitBox(ballHitBox);
        ball.setIgnoreGravity(true);
        PhysicsEngine.addPhysical(ball);
        MasterRenderer.enableSkybox(false);
        game = new PongGame(leftPaddle, rightPaddle, ball);
        initEventListeners();
        MasterRenderer.setAmbientLight(.9f);
        Scene scene = new Scene();
        scene.addEntity(topBounding);
        scene.addEntity(bottomBounding);
        scene.addEntity(leftPaddle);
        scene.addEntity(rightPaddle);
        scene.addEntity(ball);
        while(!window.isCloseRequested()) {
            game.update(window.getLastFrameTime(),klu, kld, kru, krd,flipPause);
            MasterRenderer.render(scene, camera);
            window.update();
        }
        EngineMaster.finish();
    }
    private void initEventListeners() {
        InputHandler.addListener(new InputEventListener(KEY_EVENT, KEY_PRESS, GLFW.GLFW_KEY_Q) {
            @Override
            public void onOccur() {
                klu = true;
            }
        });
        InputHandler.addListener(new InputEventListener(KEY_EVENT, KEY_PRESS, GLFW.GLFW_KEY_A) {
            @Override
            public void onOccur() {
                kld = true;
            }
        });
        InputHandler.addListener(new InputEventListener(KEY_EVENT, KEY_PRESS, GLFW.GLFW_KEY_KP_9) {
            @Override
            public void onOccur() {
                kru = true;
            }
        });
        InputHandler.addListener(new InputEventListener(KEY_EVENT, KEY_PRESS, GLFW.GLFW_KEY_KP_6) {
            @Override
            public void onOccur() {
                krd = true;
            }
        });
        InputHandler.addListener(new InputEventListener(KEY_EVENT, KEY_RELEASE, GLFW.GLFW_KEY_Q) {
            @Override
            public void onOccur() {
                klu = false;
            }
        });
        InputHandler.addListener(new InputEventListener(KEY_EVENT, KEY_RELEASE, GLFW.GLFW_KEY_A) {
            @Override
            public void onOccur() {
                kld = false;
            }
        });
        InputHandler.addListener(new InputEventListener(KEY_EVENT, KEY_RELEASE, GLFW.GLFW_KEY_KP_9) {
            @Override
            public void onOccur() {
                kru = false;
            }
        });
        InputHandler.addListener(new InputEventListener(KEY_EVENT, KEY_RELEASE, GLFW.GLFW_KEY_KP_6) {
            @Override
            public void onOccur() {
                krd = false;
            }
        });
        InputHandler.addListener(new InputEventListener(KEY_EVENT, KEY_RELEASE, GLFW.GLFW_KEY_SPACE) {
            @Override
            public void onOccur() {
                game.flipPaused();
            }
        });
    }
    public static void main(String args[]) {
        new PongGui();
    }
}
