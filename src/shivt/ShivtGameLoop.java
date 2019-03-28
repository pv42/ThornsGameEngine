package shivt;

import engine.EngineMaster;
import engine.graphics.Scene;
import engine.graphics.cameras.ThreeDimensionCamera;
import engine.graphics.display.Window;
import engine.graphics.glglfwImplementation.text.GLGuiText;
import engine.graphics.glglfwImplementation.text.GLTTFont;
import engine.graphics.glglfwImplementation.MasterRenderer;
import engine.inputs.InputLoop;
import engine.toolbox.Color;
import org.joml.Vector2f;
import shivt.guiElements.Button;
import shivt.levels.RenderLevel;

import java.util.ArrayList;
import java.util.List;

/***
 * Created by pv42 on 10.09.2016.
 */
public class ShivtGameLoop {
    private List<Button> buttons;
    private InputLoop inputLoop;
    private GLTTFont FONT;
    private float timeSinceFPSUpdate = 0;
    private int framesSinceFPSUpdate = 0;
    private GLGuiText fpsText;
    private Window window;
    private Scene scene;

    public ShivtGameLoop() {
        EngineMaster.init();
        Window window = EngineMaster.getDisplayManager().createWindow();
        buttons = new ArrayList<>();
        FONT = new GLTTFont("res/fonts/arial.ttf",64);
        fpsText = new GLGuiText(FONT, "loading", 0.0005f, new Color(0.3f, 0.3f, 0.4f), new Vector2f(0, 0));
        scene = new Scene();
        scene.addText(fpsText);
    }

    public void loop(RenderLevel level, ThreeDimensionCamera camera) {
        if (timeSinceFPSUpdate >= 1.7f) {
            fpsText.setString((int) (framesSinceFPSUpdate / timeSinceFPSUpdate) + "fps");
            // todo fix text MasterRenderer.loadText(fpsText);
            timeSinceFPSUpdate = 0;
            framesSinceFPSUpdate = 0;
        }
        if (level != null) level.process(window.getLastFrameTime());
        // todo fix text MasterRenderer.processText(fpsText);
        for (Button button : buttons) {
            button.processRender(scene);
        }
        MasterRenderer.render(scene, camera);
        window.update();
        timeSinceFPSUpdate += window.getLastFrameTime();
        framesSinceFPSUpdate++;
        //System.out.println(Conversion.normalizedDeviceCoordsFromPixelCoods(Mouse.getX(), Mouse.getY()));
    }

    public void finish() {
        EngineMaster.finish();
    }

    void addButton(Button button) {
        buttons.add(button);
        // todo fix text MasterRenderer.loadText(button.getText());
    }

    void removeAll() {
        buttons.clear();
    }

    boolean isCloseRequested() {
        return window.isCloseRequested();
    }

    public Window getWindow() {
        return window;
    }
}
