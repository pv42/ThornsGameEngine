package engine.audio;

import engine.toolbox.Log;
import org.lwjgl.openal.*;
import org.joml.Vector3f;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;
import org.lwjgl.stb.STBVorbis;


import static org.lwjgl.system.MemoryUtil.NULL;

/**
 * Created by pv42 on 02.07.16.
 */
public class AudioMaster {
    private static final String TAG = "Engine:AudioMaster";
    private static List<Integer> buffers = new ArrayList<>();
    private static ALCCapabilities alcCapabilities;
    private static long context;
    private static long device;

    public static void init() {
        Log.i(TAG, "initialising");
        try {
            device = getDefaulDeviceID();
            alcCapabilities = ALC.createCapabilities(device);

            context = ALC10.alcCreateContext(device, (IntBuffer)null);
            if (context == NULL) {
                throw new IllegalStateException("Failed to create an OpenAL context.");
            }

            //zodo alcSetThreadContext(context);
            AL.createCapabilities(alcCapabilities);





        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    public static int loadSound(String filename) {
        //todo Log.d(TAG, "loading sound " + filename);
        /*int buffer = AL10.alGenBuffers();
        buffers.add(buffer);
        //STBVorbis stbVorbis = new STBVorbis();
        //WaveData wavFile = null;
        try {
            //wavFile = WaveData.create(new BufferedInputStream(new FileInputStream("res/sounds/" + filename + ".wav")));
        } catch (FileNotFoundException e) {
            Log.e("faild reading sound: ");
            e.printStackTrace();
        }
        if (wavFile == null) Log.e("sound is null");
        AL10.alBufferData(buffer, wavFile.format, wavFile.data, wavFile.samplerate);
        wavFile.dispose();
        return buffer;*/
        return 0;
    }

    public static void setListenerData() {
        setListenerData(new Vector3f(), new Vector3f());
    }

    public static void setListenerData(Vector3f position) {
        setListenerData(position, new Vector3f());
    }

    public static void setListenerData(Vector3f position, Vector3f velocity) {
        AL10.alListener3f(AL10.AL_POSITION, position.x(), position.y(), position.z());
        AL10.alListener3f(AL10.AL_VELOCITY, velocity.x(), velocity.y(), velocity.z());
    }


    public static void cleanUp() {
        for (int buffer : buffers) {
            AL10.alDeleteBuffers(buffer);
            ALC.destroy();
        }
        // todo al.destroy
    }

    private static long getDefaulDeviceID() {
        long device = ALC10.alcOpenDevice((ByteBuffer)null);
        if (device == NULL) {
            throw new IllegalStateException("Failed to open the default device.");
        }
        return device;
    }
}
