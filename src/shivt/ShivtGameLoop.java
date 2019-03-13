package shivt;

import engine.EngineMaster;
import engine.graphics.cameras.ThreeDimensionCamera;
import engine.graphics.display.Window;
import engine.graphics.glglfwImplementation.display.GLFWDisplayManager;
import engine.graphics.glglfwImplementation.display.GLFWWindow;
import engine.graphics.fontMeshCreator.FontType;
import engine.graphics.fontMeshCreator.GUIText;
import engine.inputs.InputLoop;
import engine.graphics.renderEngine.Loader;
import engine.graphics.renderEngine.MasterRenderer;
import org.joml.Vector2f;
import org.joml.Vector4f;
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
    private FontType FONT;
    private float timeSinceFPSUpdate = 0;
    private int framesSinceFPSUpdate = 0;
    private GUIText fpsText;
    private Window window;
    public ShivtGameLoop() {
        Window window = EngineMaster.init();
        buttons = new ArrayList<>();
        FONT = Loader.loadFont("courier_df");
        fpsText = new GUIText("loading", 1, FONT, new Vector2f(0, 0), 1, false);
        fpsText.setColor(0.3f, 0.3f, 0.4f);
        MasterRenderer.loadText(fpsText);
    }
    public void loop(RenderLevel level,ThreeDimensionCamera camera) {
        if (timeSinceFPSUpdate >= 1.7f) {
            fpsText = new GUIText((int) (framesSinceFPSUpdate / timeSinceFPSUpdate) + "fps", 1, FONT, new Vector2f(), 1, false);
            fpsText.setColor(1.0f, 0.0f, 0.0f);
            fpsText.setBorderColor(1, .8f, 0);
            fpsText.setBorderWidth(0.1f);
            fpsText.setEdge(0.1f);
            MasterRenderer.loadText(fpsText);
            timeSinceFPSUpdate = 0;
            framesSinceFPSUpdate = 0;
        }
        if(level != null) level.process(window.getLastFrameTime());
        MasterRenderer.processText(fpsText);
        buttons.forEach(Button::processRender);
        MasterRenderer.render( camera);
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
        MasterRenderer.loadText(button.getText());
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
