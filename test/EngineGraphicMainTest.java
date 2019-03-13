import engine.EngineMaster;
import engine.graphics.cameras.Camera;
import engine.graphics.cameras.StaticThreeDimensionCamera;
import engine.graphics.glglfwImplementation.display.GLFWWindow;
import engine.graphics.glglfwImplementation.entities.GLEntity;
import engine.graphics.lights.Light;
import engine.graphics.models.RawModel;
import engine.graphics.models.TexturedModel;
import engine.graphics.renderEngine.Loader;
import engine.graphics.renderEngine.MasterRenderer;
import engine.graphics.textures.ModelTexture;
import engine.toolbox.Color;
import engine.toolbox.Log;
import engine.toolbox.OBJLoader;
import org.joml.Vector3f;

import java.util.LinkedList;
import java.util.List;


public class EngineGraphicMainTest {
    public static void main(String[] args) {
        GLFWWindow window = EngineMaster.init();
        window.setTitle("??");
        RawModel model = OBJLoader.loadObjModel("barrel");
        ModelTexture texture = new ModelTexture(Loader.loadTexture("tree.png"));
        texture.setReflectivity(.0f);
        texture.setShineDamper(.1f);
        TexturedModel tm = new TexturedModel(model, texture);
        GLEntity entity = new GLEntity(tm, new Vector3f());
        entity.setScale(.3f);
        Camera camera = new StaticThreeDimensionCamera(new Vector3f(0,0,20), new Vector3f());
        List<Light> lights = new LinkedList<>();
        lights.add(new Light(new Vector3f(0, 50000, 20000), new Color(1, 1, .9), new Vector3f(1f, 0.00f, 0.00f)));
        MasterRenderer.addEntity(entity);
        MasterRenderer.enableCulling();
        MasterRenderer.disableCulling();
        lights.forEach(MasterRenderer::addLight);
        Log.i("MainTest", "start rendering");
        while (!window.isCloseRequested()){
            MasterRenderer.render(camera);
            entity.increaseRotation(0.1f,0,0);
            window.update();
        }
        EngineMaster.finish();
    }
}
