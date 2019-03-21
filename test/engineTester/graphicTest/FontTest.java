package engineTester.graphicTest;

import engine.EngineMaster;
import engine.graphics.cameras.Camera;
import engine.graphics.cameras.StaticThreeDimensionCamera;
import engine.graphics.display.Window;
import engine.graphics.glglfwImplementation.text.GLGuiText;
import engine.graphics.glglfwImplementation.text.GLTTFont;
import engine.graphics.renderEngine.MasterRenderer;
import engine.toolbox.Color;
import engine.toolbox.Log;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class FontTest {
    @Test
    public void testFont() {
        Window window = EngineMaster.init();
        GLTTFont font = new GLTTFont("res\\fonts\\arial.ttf", 128);
        GLGuiText text = new GLGuiText(font, "H3l!0 w0r!d", 0.0001f, new Color(0, 1.0, 1.0), new Vector2f());
        Camera camera = new StaticThreeDimensionCamera(new Vector3f(), new Vector3f());
        int count = 0;
        while (!window.isCloseRequested() && count < 60) {
            MasterRenderer.render(camera);
            window.update();
            MasterRenderer.addText(text);
            count++;
        }
        EngineMaster.finish();
        assertEquals(0, Log.getErrorNumber());
        assertEquals(0, Log.getWarningNumber());

    }
}
