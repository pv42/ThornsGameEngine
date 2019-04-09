package engine.graphics;

import engine.EngineMaster;
import engine.graphics.cameras.Camera;
import engine.graphics.cameras.StaticThreeDimensionCamera;
import engine.graphics.display.Window;
import engine.graphics.glglfwImplementation.MasterRenderer;
import engine.graphics.glglfwImplementation.entities.GLEntity;
import engine.graphics.glglfwImplementation.models.GLMaterializedModel;
import engine.graphics.glglfwImplementation.models.GLRawModel;
import engine.graphics.glglfwImplementation.textures.GLModelTexture;
import engine.graphics.lights.Light;
import engine.graphics.materials.TexturedMaterial;
import engine.toolbox.Color;
import engine.toolbox.Log;
import engine.toolbox.MeshCreator;
import engine.toolbox.OBJLoader;
import org.joml.Vector3f;
import org.junit.jupiter.api.Test;

import java.util.LinkedList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;


public class EngineGraphicMainTest {
    @Test
    void test() {
        Log.clearNumbers();
        EngineMaster.init();
        Window window = EngineMaster.getDisplayManager().createWindow();
        window.setTitle("??");
        GLRawModel model = OBJLoader.loadObjModel("barrel");
        GLModelTexture texture = (GLModelTexture) EngineMaster.getTextureLoader().loadTexture("tree.png");
        TexturedMaterial material = new TexturedMaterial(texture);
        material.setReflectivity(0f);
        material.setShineDamper(.1f);
        GLMaterializedModel tm = new GLMaterializedModel(model, material);
        GLEntity entity = new GLEntity(tm, new Vector3f());
        entity.setScale(.3f);
        // wireframe
        TexturedMaterial wfmaterial = new TexturedMaterial(texture);
        wfmaterial.setWireframe(true);
        GLMaterializedModel wfmodel = new GLMaterializedModel(MeshCreator.createBox(5, 3, 4), wfmaterial);
        GLEntity wfEntity = new GLEntity(wfmodel, new Vector3f(6, 8, 0));

        // --
        Camera camera = new StaticThreeDimensionCamera(new Vector3f(0, 0, 20), new Vector3f());
        List<Light> lights = new LinkedList<>();
        lights.add(new Light(new Vector3f(0, 50000, 20000), new Color(1, 1, .9), new Vector3f(1f, 0.00f, 0.00f)));
        Scene scene = new Scene();
        scene.addEntity(entity);
        scene.addEntity(wfEntity);
        MasterRenderer.enableCulling();
        MasterRenderer.disableCulling();
        lights.forEach(scene::addLight);
        Log.i("MainTest", "start rendering");
        int iter = 0;
        while (!window.isCloseRequested() && iter < 180) {
            MasterRenderer.render(scene, camera);
            entity.increaseRotation(16f, 10f, 3f);
            window.update();
            iter++;
        }
        assertEquals(0, Log.getErrorNumber());
        assertEquals(0, Log.getWarningNumber());
        window.destroy();
        EngineMaster.finish();
    }
}
