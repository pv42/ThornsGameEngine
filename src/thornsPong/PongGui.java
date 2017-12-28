package thornsPong;

import engine.EngineMaster;
import engine.graphics.cameras.TwoDimensionsCamera;
import engine.graphics.display.DisplayManager;
import engine.graphics.models.TexturedModel;
import engine.graphics.renderEngine.Loader;
import engine.graphics.renderEngine.MasterRenderer;
import engine.graphics.textures.ModelTexture;
import engine.inputs.InputHandler;
import engine.inputs.listeners.InputEventListener;
import engine.physics.CuboidHitBox;
import engine.physics.HitBox;
import engine.physics.PhysicalEntity;
import engine.physics.PhysicsEngine;
import engine.toolbox.MeshCreator;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.glfw.GLFW;

import static engine.inputs.InputEvent.KEY_EVENT;
import static engine.inputs.InputEvent.KEY_PRESS;
import static engine.inputs.InputEvent.KEY_RELEASE;

public class PongGui {
    private boolean klu = false;
    private boolean kld = false;
    private boolean kru = false;
    private boolean krd = false;
    private boolean flipPause = false;
    private PongGame game;
    public PongGui() {
        EngineMaster.init(true);
        TwoDimensionsCamera camera = new TwoDimensionsCamera();
        ModelTexture texture = new ModelTexture(Loader.loadTexture("white.png"));
        TexturedModel ballModel = new TexturedModel(MeshCreator.createCircle(.04f,24),texture);
        TexturedModel paddleModel = new TexturedModel(MeshCreator.createBox(.2f,.2f,.2f),texture);
        HitBox paddleHitBox = new CuboidHitBox(-.1f,.1f,-.1f,.1f,-.1f,.1f);
        PhysicalEntity leftPaddle = new PhysicalEntity(paddleModel, new Vector3f(),1);
        leftPaddle.setStatic(true);
        leftPaddle.setHitBox(paddleHitBox);
        leftPaddle.setIgnoreGravity(true);
        PhysicsEngine.addPhysical(leftPaddle);
        PhysicalEntity rightPaddle = new PhysicalEntity(paddleModel, new Vector3f(),1);
        rightPaddle.setStatic(true);
        rightPaddle.setHitBox(paddleHitBox);
        rightPaddle.setIgnoreGravity(true);
        PhysicsEngine.addPhysical(rightPaddle);
        HitBox ballHitBox = new CuboidHitBox(-.02f,.02f,-.02f,.02f,-.02f,.02f);
        PhysicalEntity ball = new PhysicalEntity(ballModel,new Vector3f(),0);
        ball.setHitBox(ballHitBox);
        ball.setIgnoreGravity(true);
        PhysicsEngine.addPhysical(ball);
        Vector4f clipPlane = new Vector4f();
        MasterRenderer.enableSkybox(false);
        game = new PongGame(leftPaddle, rightPaddle, ball);
        initEventListeners();
        MasterRenderer.setAmbientLight(.9f);
        MasterRenderer.addEntity(leftPaddle);
        MasterRenderer.addEntity(rightPaddle);
        MasterRenderer.addEntity(ball);
        while(!DisplayManager.isCloseRequested()) {
            game.update(DisplayManager.getFrameTimeSeconds(),klu, kld, kru, krd,flipPause);
            MasterRenderer.render(camera,clipPlane);
            DisplayManager.updateDisplay();
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
