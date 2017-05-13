package engine.audio;

import org.lwjgl.openal.AL;
import org.lwjgl.openal.AL10;
import org.lwjgl.openal.ALCCapabilities;
import org.lwjgl.openal.ALCapabilities;
import org.lwjgl.util.WaveData;
import engine.toolbox.Log;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by pv42 on 02.07.16.
 */
public class AudioMaster {
    private static final String TAG = "Engine:AudioMaster";
    private static List<Integer> buffers = new ArrayList<>();
    private static ALCapabilities capabilities;
    public static void init() {
        Log.i(TAG, "initialising");
        try {
            //todo AL
            //capabilities = AL.createCapabilities();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static int loadSound(String filename) {
        Log.d(TAG, "loading sound " +filename);
        int buffer = AL10.alGenBuffers();
        buffers.add(buffer);
        WaveData wavFile = null;
        try {
            wavFile = WaveData.create(new BufferedInputStream(new FileInputStream("res/sounds/" +  filename + ".wav")));
        } catch (FileNotFoundException e) {
            Log.e("faild reading sound: " );
            e.printStackTrace();
        }
        if(wavFile == null) Log.e("sound is null");
        AL10.alBufferData(buffer,wavFile.format,wavFile.data,wavFile.samplerate);
        wavFile.dispose();
        return buffer;
    }
    public static void setListenerData() {
        AL10.alListener3f(AL10.AL_POSITION,0,0,0);
        AL10.alListener3f(AL10.AL_VELOCITY,0,0,0);
    }
    public static void cleanUp() {
        for(int buffer: buffers) {
            AL10.alDeleteBuffers(buffer);
        }
        // todo al.destroy
    }
}
