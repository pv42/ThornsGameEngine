package engine.graphics;

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
    void testFont() {
        Log.clearNumbers();
        EngineMaster.init();
        Window window = EngineMaster.getDisplayManager().createWindow();
        GLTTFont bahnschrift = EngineMaster.getFontFactory().loadSystemFont("bahnschrift",128);
        GLTTFont arial = EngineMaster.getFontFactory().loadSystemFont("arial",32);
        GLTTFont bahnschrift2 = EngineMaster.getFontFactory().loadSystemFont("bahnschrift",32);
        GLGuiText text = new GLGuiText(bahnschrift, "H31!0 w0r!d", 0.0001f, new Color(1.0, 1.0, 1.0), new Vector2f());
        GLGuiText text1 = new GLGuiText(arial, "-0.5  0.5", 0.0002f, new Color(0.5, 1, 0), new Vector2f(-.5f,.5f));
        GLGuiText text2 = new GLGuiText(bahnschrift2, "bahn2_-|.", 0.0002f, new Color(0.3, 0, 0), new Vector2f(-.5f,-.5f));
        Camera camera = new StaticThreeDimensionCamera(new Vector3f(), new Vector3f());
        Scene scene = new Scene();
        int count = 0;
        scene.addText(text);
        scene.addText(text1);
        scene.addText(text2);
        while (!window.isCloseRequested() && count < 60) {
            MasterRenderer.render(scene, camera);
            window.update();
            text.setPosition(new Vector2f(count/60f -.5f,0f));
            count++;
        }
        EngineMaster.finish();
        assertEquals(32,arial.getPixelSize());
        assertEquals(128,bahnschrift.getPixelSize());
        assertEquals(bahnschrift,bahnschrift2);
        assertEquals(0, Log.getErrorNumber());
        assertEquals(0, Log.getWarningNumber());

    }
}
