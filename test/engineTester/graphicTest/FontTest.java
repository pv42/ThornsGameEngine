package engineTester.graphicTest;

import engine.EngineMaster;
import engine.graphics.Scene;
import engine.graphics.cameras.Camera;
import engine.graphics.cameras.StaticThreeDimensionCamera;
import engine.graphics.display.Window;
import engine.graphics.glglfwImplementation.text.GLGuiText;
import engine.graphics.glglfwImplementation.text.GLTTFont;
import engine.graphics.glglfwImplementation.MasterRenderer;
import engine.graphics.glglfwImplementation.text.GLTTFontFactory;
import engine.toolbox.Color;
import engine.toolbox.Log;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class FontTest {
    @Test
    public void testFont() {
        int now = Log.getWarningNumber();
        EngineMaster.init();
        Window window = EngineMaster.getDisplayManager().createWindow();
        GLTTFont font = EngineMaster.getFontFactory().loadSystemFont("bahnschrift",128);
        GLGuiText text = new GLGuiText(font, "H31!0 w0r!d", 0.0001f, new Color(0, 1.0, 1.0), new Vector2f());
        Camera camera = new StaticThreeDimensionCamera(new Vector3f(), new Vector3f());
        Scene scene = new Scene();
        int count = 0;
        scene.addText(text);
        while (!window.isCloseRequested() && count < 60) {
            MasterRenderer.render(scene, camera);
            window.update();
            count++;
        }
        EngineMaster.finish();
        assertEquals(0, Log.getErrorNumber());
        assertEquals(now, Log.getWarningNumber());

    }
}
