package engineTester.graphicTest;

import engine.EngineMaster;
import engine.graphics.cameras.ThreeDimensionCamera;
import engine.graphics.glglfwImplementation.display.GLFWWindow;
import engine.graphics.glglfwImplementation.entities.GLEntity;
import engine.graphics.models.RawModel;
import engine.graphics.models.TexturedModel;
import engine.graphics.particles.ParticleMaster;
import engine.graphics.renderEngine.Loader;
import engine.graphics.renderEngine.MasterRenderer;
import engine.graphics.textures.ModelTexture;
import engine.inputs.InputHandler;
import engine.toolbox.collada.ColladaLoader;
import org.joml.Vector3f;
import shivt.ShivtCamera;
// todo fixme
public class Main {
    static boolean useEngine = false;
    public static void main(String args[]) {
        GLFWWindow window = EngineMaster.init();

        TestRender renderer = null;
        if (useEngine) {
            MasterRenderer.init(window, false);

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
        GLEntity lara  = new GLEntity(ColladaLoader.loadCollada("Lara_Croft").getTexturedModels(),new Vector3f(0,12.5f,0));
        lara.setRx(80);
        int texture = Loader.loadTexture("Screen_Dust_D.png");
        ModelTexture modelTexture = new ModelTexture(texture);
        TexturedModel texturedModel = new TexturedModel(model, modelTexture);

        ThreeDimensionCamera camera = new ShivtCamera();
        GLEntity e = new GLEntity(texturedModel, new Vector3f(0,12.5f,1f));

        while (!window.isCloseRequested()) {
            if (useEngine) {
                MasterRenderer.addEntity(lara);
                MasterRenderer.addEntity(e);
                MasterRenderer.render(camera);
            } else {
                renderer.prepare();
                renderer.render(lara, camera);

                renderer.render(e, camera);
            }
            //window.destroy();
            //GLFWDisplayManager.destroy();
        }
        if(useEngine) {
            MasterRenderer.cleanUp();
        } else {
            renderer.cleanUp();
            Loader.cleanUp();
        }

        window.destroy();
        EngineMaster.finish();

    }
}
