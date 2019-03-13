import engine.EngineMaster;
import engine.audio.AudioMaster;
import engine.audio.OggData;
import engine.audio.Source;
import engine.graphics.glglfwImplementation.display.GLFWDisplayManager;
import engine.graphics.glglfwImplementation.display.GLFWWindow;

/**
 * Created by pv42 on 10.07.2017.
 */
public class EngineSoundTest {
    public static void main(String[] args) {
        GLFWWindow window = EngineMaster.init();
        Source source = new Source();
        OggData ogg = AudioMaster.loadSound("res/sounds/GT_Ogg_Vorbis.ogg");
        if (!source.play(ogg)) {
            System.err.println("Playback failed. (I)");
            window.destroy();
            EngineMaster.finish();
        }
        while (!window.isCloseRequested()) {

            if (!source.update(ogg)) {
                System.err.println("Playback failed. (II)");
                break;
            }
            window.update();
        }
        EngineMaster.finish();
    }

}
