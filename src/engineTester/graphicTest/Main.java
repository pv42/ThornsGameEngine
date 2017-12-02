package engineTester.graphicTest;

import engine.graphics.cameras.ThreeDimensionCamera;
import engine.graphics.display.Window;
import engine.graphics.entities.Entity;
import engine.graphics.models.RawModel;
import engine.graphics.models.TexturedModel;
import engine.graphics.particles.ParticleMaster;
import engine.graphics.display.DisplayManager;
import engine.graphics.renderEngine.Loader;
import engine.graphics.renderEngine.MasterRenderer;
import engine.graphics.textures.ModelTexture;
import engine.inputs.InputHandler;
import engine.toolbox.collada.ColladaLoader;
import org.joml.Vector3f;
import org.joml.Vector4f;
import shivt.ShivtCamera;

public class Main {
    static boolean useEngine = true;
    public static void main(String args[]) {
        Window window = DisplayManager.createWindow();
        TestRender renderer = null;
        if (useEngine) {
            MasterRenderer.init(false);

            ParticleMaster.init(MasterRenderer.getProjectionMatrix());
        } else {
            renderer = new TestRender();
        }
        InputHandler.init(window.getId());
        float[] vertices = {
                -0.5f, 0.5f, 0,
                -0.5f, -0.5f, 0,
                0.5f, -0.5f, 0,
                0.5f, 0.5f, 0f};
        int[] indices = {0, 1, 3, 3, 1, 2};
        float[] textCoords = {
                0, 0,
                0, 1,
                1, 1,
                1, 0
        };
        float[] normal = {
                0, 0, 0
        };
        RawModel model = Loader.loadToVAO(vertices, textCoords, normal, indices);
        Entity lara  = new Entity(ColladaLoader.loadCollada("Lara_Croft").getTexturedModels(),new Vector3f(0,12.5f,0));
        lara.setRx(80);
        int texture = Loader.loadTexture("Screen_Dust_D.png");
        ModelTexture modelTexture = new ModelTexture(texture);
        TexturedModel texturedModel = new TexturedModel(model, modelTexture);

        ThreeDimensionCamera camera = new ShivtCamera();
        Entity e = new Entity(texturedModel, new Vector3f(0,12.5f,1f));

        while (!DisplayManager.isCloseRequested()) {
            if (useEngine) {
                MasterRenderer.addEntity(lara);
                MasterRenderer.addEntity(e);
                MasterRenderer.render(camera, new Vector4f(0, -1, 0, 100000));
            } else {
                renderer.prepare();
                renderer.render(lara, camera);

                renderer.render(e, camera);
            }
            DisplayManager.updateDisplay();
        }
        if(useEngine) {
            MasterRenderer.cleanUp();
        } else {
            renderer.cleanUp();
            Loader.cleanUp();
        }

        DisplayManager.destroyDisplay();

    }
}
