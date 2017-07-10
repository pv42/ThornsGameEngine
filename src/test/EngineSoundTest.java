package test;

import engine.EngineMaster;
import engine.audio.AudioMaster;
import engine.audio.OggSource;
import engine.audio.Source;
import engine.graphics.renderEngine.DisplayManager;

/**
 * Created by pv42 on 10.07.2017.
 */
public class EngineSoundTest {
    public static void main(String args[]) {
        EngineMaster.init();
        Source source = new Source();
        OggSource ogg = AudioMaster.loadSound("res/sounds/GT_Ogg_Vorbis.ogg");
        source.play(ogg);
        while (!DisplayManager.isCloseRequested()) {
            DisplayManager.updateDisplay();
            source.play(ogg);
        }
        EngineMaster.finish();
    }

}
