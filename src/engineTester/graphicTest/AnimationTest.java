package engineTester.graphicTest;

import engine.EngineMaster;
import engine.graphics.animation.Animation;
import engine.graphics.cameras.Camera;
import engine.graphics.cameras.StaticThreeDimensionCamera;
import engine.graphics.display.DisplayManager;
import engine.graphics.display.Window;
import engine.graphics.entities.Entity;
import engine.graphics.models.TexturedModel;
import engine.graphics.renderEngine.MasterRenderer;
import engine.inputs.InputHandler;
import engine.inputs.InputLoop;
import engine.inputs.listeners.InputEventListener;
import engine.toolbox.Log;
import engine.toolbox.collada.Collada;
import engine.toolbox.collada.ColladaLoader;
import org.joml.Vector3f;
import org.joml.Vector4f;
import shivt.ShivtCamera;


public class AnimationTest {
    public static void main(String args[]) {
        Window window = EngineMaster.init();
        Collada collada = ColladaLoader.loadCollada("cowboy");
        TexturedModel cowboyModel = collada.getTexturedModels().get(0);
        Animation animation = collada.getAnimation();
        Entity entity = new Entity(cowboyModel,new Vector3f(0,-12f,0),0,0,0,1);
        Camera camera = new ShivtCamera();
        InputHandler.init(window.getId());
        MasterRenderer.addEntity(entity);
        while (!DisplayManager.isCloseRequested()) {
            MasterRenderer.render(camera,new Vector4f());
            DisplayManager.updateDisplay(window);
            try {
                Thread.sleep(60);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        EngineMaster.finish();
    }
}
