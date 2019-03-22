import engine.EngineMaster;
import engine.audio.AudioMaster;
import engine.audio.OggData;
import engine.audio.Source;
import engine.graphics.glglfwImplementation.display.GLFWWindow;
import engine.toolbox.Log;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Created by pv42 on 10.07.2017.
 */
public class EngineSoundTest {
    @Test
    void testSound() throws InterruptedException {
        Thread.sleep(1000);
        GLFWWindow window = EngineMaster.init();
        Source source = new Source();
        OggData ogg = AudioMaster.loadSound("res/sounds/GT_Ogg_Vorbis.ogg");

        //MasterRenderer.render(new StaticThreeDimensionCamera(new Vector3f(), new Vector3f()));
        if (!source.play(ogg)) {
            System.err.println("Playback failed. (I)");
        }
        while (!window.isCloseRequested()) {

            if (!source.update(ogg)) {
                System.err.println("Playback failed. (II)");
                break;
            }
            //window.update();
        }
        window.destroy();

        EngineMaster.finish();

        assertEquals(0, Log.getErrorNumber());
        assertEquals(0, Log.getWarningNumber());
    }

}
