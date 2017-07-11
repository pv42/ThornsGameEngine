package test;

import engine.EngineMaster;
import engine.audio.AudioMaster;
import engine.audio.OggData;
import engine.audio.Source;
import engine.graphics.renderEngine.DisplayManager;

/**
 * Created by pv42 on 10.07.2017.
 */
public class EngineSoundTest {
    public static void main(String args[]) {
        EngineMaster.init();
        Source source = new Source();
        OggData ogg = AudioMaster.loadSound("res/sounds/GT_Ogg_Vorbis.ogg");
        if (!source.play(ogg)) {
            System.err.println("Playback failed.");
            DisplayManager.destroyDisplay();
        }
        while (!DisplayManager.isCloseRequested()) {

            if (!source.update(ogg)) {
                System.err.println("Playback failed.");
                DisplayManager.destroyDisplay();
            }
            DisplayManager.updateDisplay();
        }
        EngineMaster.finish();
    }

}
