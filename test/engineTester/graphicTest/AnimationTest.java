package engineTester.graphicTest;

import engine.EngineMaster;
import engine.graphics.animation.Animation;
import engine.graphics.cameras.Camera;
import engine.graphics.glglfwImplementation.display.GLFWWindow;
import engine.graphics.glglfwImplementation.entities.GLEntity;
import engine.graphics.models.TexturedModel;
import engine.graphics.renderEngine.MasterRenderer;
import engine.inputs.InputHandler;
import engine.toolbox.collada.Collada;
import engine.toolbox.collada.ColladaLoader;
import org.joml.Vector3f;
import shivt.ShivtCamera;


public class AnimationTest {
    public static void main(String args[]) {
        GLFWWindow window = EngineMaster.init();
        Collada collada = ColladaLoader.loadCollada("cowboy");
        TexturedModel cowboyModel = collada.getTexturedModels().get(0);
        Animation animation = collada.getAnimation();
        GLEntity entity = new GLEntity(cowboyModel,new Vector3f(0,-12f,0),0,0,0,1);
        Camera camera = new ShivtCamera();
        InputHandler.init(window.getId());
        MasterRenderer.addEntity(entity);
        while (!window.isCloseRequested()) {
            MasterRenderer.render(camera);
            window.update();
            try {
                Thread.sleep(60);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        EngineMaster.finish();
    }
}
