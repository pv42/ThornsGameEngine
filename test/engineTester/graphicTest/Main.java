package engineTester.graphicTest;

import engine.EngineMaster;
import engine.graphics.Scene;
import engine.graphics.cameras.ThreeDimensionCamera;
import engine.graphics.glglfwImplementation.GLLoader;
import engine.graphics.glglfwImplementation.display.GLFWWindow;
import engine.graphics.glglfwImplementation.entities.GLEntity;
import engine.graphics.glglfwImplementation.models.GLRawModel;
import engine.graphics.glglfwImplementation.models.GLTexturedModel;
import engine.graphics.particles.ParticleMaster;
import engine.graphics.glglfwImplementation.MasterRenderer;
import engine.graphics.glglfwImplementation.textures.ModelTexture;
import engine.inputs.InputHandler;
import engine.toolbox.collada.ColladaLoader;
import org.joml.Vector3f;
import shivt.ShivtCamera;
// todo fixme
public class Main {
    static boolean useEngine = true;
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
        GLRawModel model = GLLoader.loadToVAO(vertices, textCoords, normal, indices);
        GLEntity lara  = new GLEntity(ColladaLoader.loadCollada("Lara_Croft").getTexturedModels(),new Vector3f(0,12.5f,0));
        lara.setRx(80);
        int texture = GLLoader.loadTexture("Screen_Dust_D.png");
        ModelTexture modelTexture = new ModelTexture(texture);
        GLTexturedModel texturedModel = new GLTexturedModel(model, modelTexture);

        ThreeDimensionCamera camera = new ShivtCamera();
        GLEntity e = new GLEntity(texturedModel, new Vector3f(0,12.5f,1f));
        Scene scene = new Scene();
        while (!window.isCloseRequested()) {
            if (useEngine) {
                scene.addEntity(lara);
                scene.addEntity(e);
                MasterRenderer.render(scene,camera);
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
            GLLoader.cleanUp();
        }

        window.destroy();
        EngineMaster.finish();

    }
}
