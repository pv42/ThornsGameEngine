package thornsPong;

import engine.EngineMaster;
import engine.graphics.cameras.TwoDimensionsCamera;
import engine.graphics.display.DisplayManager;
import engine.graphics.entities.Entity;
import engine.graphics.models.RawModel;
import engine.graphics.models.TexturedModel;
import engine.graphics.renderEngine.Loader;
import engine.graphics.renderEngine.MasterRenderer;
import engine.graphics.textures.ModelTexture;
import engine.inputs.InputEvent;
import engine.inputs.InputHandler;
import engine.inputs.InputLoop;
import engine.inputs.listeners.InputEventListener;
import engine.toolbox.OBJLoader;
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
    public PongGui() {
        PongGame game = new PongGame();
        EngineMaster.init(true);
        TwoDimensionsCamera camera = new TwoDimensionsCamera();
        ModelTexture texture = new ModelTexture(Loader.loadTexture("white.png"));
        RawModel model = OBJLoader.loadObjModel("cube");
        TexturedModel ballModel = new TexturedModel(model,texture);
        TexturedModel racketModel = new TexturedModel(model,texture);
        Entity leftRacket = new Entity(racketModel, new Vector3f(),0,0,0,0.1f);
        Entity rightRacket = new Entity(racketModel, new Vector3f(), 0, 0, 0, 0.1f);
        Entity ball = new Entity(ballModel, new Vector3f(), 0,0,0, .03f);
        Vector4f clipPlane = new Vector4f();
        MasterRenderer.enableSkybox(false);
        initEventListeners();
        while(!DisplayManager.isCloseRequested()) {
            game.update(DisplayManager.getFrameTimeSeconds(),klu, kld, kru, krd);
            leftRacket.setPosition(-0.75f, game.getLeft_y(),0);
            rightRacket.setPosition(0.75f, game.getRight_y(),0);
            ball.setPosition(game.getBallPosition().x(), game.getBallPosition().y(), 0);
            MasterRenderer.processEntity(leftRacket);
            MasterRenderer.processEntity(rightRacket);
            MasterRenderer.processEntity(ball);
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
    }
    public static void main(String args[]) {
        new PongGui();
    }
}
