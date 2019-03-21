package engineTester.graphicTest;

import engine.EngineMaster;
import engine.graphics.animation.Animation;
import engine.graphics.cameras.Camera;
import engine.graphics.glglfwImplementation.display.GLFWWindow;
import engine.graphics.glglfwImplementation.entities.GLEntity;
import engine.graphics.models.TexturedModel;
import engine.graphics.renderEngine.MasterRenderer;
import engine.inputs.InputHandler;
import engine.toolbox.Log;
import engine.toolbox.collada.Collada;
import engine.toolbox.collada.ColladaLoader;
import org.joml.Vector3f;
import org.junit.jupiter.api.Test;
import shivt.ShivtCamera;

import static org.junit.jupiter.api.Assertions.assertEquals;


public class AnimationTest {
    @Test
    void testAnimation() {
        GLFWWindow window = EngineMaster.init();
        Collada collada = ColladaLoader.loadCollada("cowboy");
        TexturedModel cowboyModel = collada.getTexturedModels().get(0);
        Animation animation = collada.getAnimation();
        GLEntity entity = new GLEntity(cowboyModel,new Vector3f(0,-12f,0),0,0,0,1);
        Camera camera = new ShivtCamera();
        InputHandler.init(window.getId());
        MasterRenderer.addEntity(entity);
        int count = 0;
        while (!window.isCloseRequested() && count < 10) {
            MasterRenderer.render(camera);
            window.update();
            try {
                Thread.sleep(60);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            count++;
        }
        EngineMaster.finish();
        assertEquals(0, Log.getErrorNumber());
        //TODO fix warnings
        assertEquals(0, Log.getWarningNumber());
    }
}
