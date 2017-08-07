package test.graphicTest;

import engine.graphics.models.RawModel;
import engine.graphics.models.TexturedModel;
import engine.graphics.renderEngine.DisplayManager;
import engine.graphics.renderEngine.Loader;
import engine.graphics.shaders.SimpleShader;
import engine.graphics.textures.ModelTexture;

public class Main {
    public static void main(String args[]) {

        DisplayManager.createDisplay();

        TestRender renderer  = new TestRender();

        SimpleShader shader = new SimpleShader();
        float[] vertices = {
                -0.5f, 0.5f, 0,
                -0.5f, -0.5f, 0,
                0.5f, -0.5f, 0,
                0.5f, 0.5f, 0f };
        int[] indices = { 0,1,3, 3,1,2 };
        float[] textCoords = {
                0,0,
                0,1,
                1,1,
                1,0
        };
        RawModel model = Loader.loadToVAO(vertices,textCoords, indices);
        int texture = Loader.loadTexture("Screen_Dust_D.png");
        ModelTexture modelTexture = new ModelTexture(texture);
        TexturedModel texturedModel = new TexturedModel(model,modelTexture);

        while (!DisplayManager.isCloseRequested()) {
            renderer.prepare();
            shader.start();
            renderer.render(texturedModel);
            shader.stop();
            DisplayManager.updateDisplay();
        }
        shader.cleanUp();
        Loader.cleanUp();
        DisplayManager.destroyDisplay();

    }
}
