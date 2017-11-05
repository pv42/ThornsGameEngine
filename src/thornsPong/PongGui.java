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
import engine.toolbox.OBJLoader;
import org.joml.Vector3f;
import org.joml.Vector4f;

public class PongGui {
    public PongGui() {
        PongGame game = new PongGame();
        EngineMaster.init();
        TwoDimensionsCamera camera = new TwoDimensionsCamera();
        ModelTexture texture = new ModelTexture(Loader.loadTexture("white.png"));
        RawModel model = OBJLoader.loadObjModel("barrel");
        TexturedModel ballModel = new TexturedModel(model,texture);
        TexturedModel racketModel = new TexturedModel(model,texture);
        Entity leftRacket = new Entity(racketModel, new Vector3f(),0,0,0,1);
        Entity rightRacket = new Entity(racketModel, new Vector3f(), 0, 0, 0, 1);
        Entity ball = new Entity(ballModel, new Vector3f(), 0,0,0, 0.1f);
        Vector4f clipPlane = new Vector4f();
        MasterRenderer.enableSkybox(false);
        while(!DisplayManager.isCloseRequested()) {
            leftRacket.setPosition(-0.8f, game.getLeft_y(),0);
            rightRacket.setPosition(0.8f, game.getRight_y(),0);
            ball.setPosition(game.getBallPosition().x(), game.getBallPosition().y(), 0);
            MasterRenderer.processEntity(leftRacket);
            MasterRenderer.processEntity(rightRacket);
            MasterRenderer.processEntity(ball);
            MasterRenderer.render(camera,clipPlane);
            DisplayManager.updateDisplay();
        }
        //MasterRenderer.processEntity();
        EngineMaster.finish();
    }
    public static void main(String args[]) {
        new PongGui();
    }
}
