package engine.audio;

import engine.toolbox.IOUtil;
import engine.toolbox.Log;
import org.lwjgl.BufferUtils;
import org.lwjgl.openal.*;
import org.joml.Vector3f;

import java.io.IOException;
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
            device = openDefaultDevice();
            alcCapabilities = ALC.createCapabilities(device);
            context = ALC10.alcCreateContext(device, (IntBuffer)null);
            if (context == NULL) {
                throw new IllegalStateException("Failed to create an OpenAL context.");
            }
            EXTThreadLocalContext.alcSetThreadContext(context);
            AL.createCapabilities(alcCapabilities);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    public static OggData loadSound(String filename) {
        Log.d(TAG, "loading sound " + filename);
        ByteBuffer vorbis = null;
        try {
            vorbis = IOUtil.ioResourceToByteBuffer(filename,256 * 1024);
        } catch (IOException e) {
            e.printStackTrace();
        }
        IntBuffer errorBuffer = BufferUtils.createIntBuffer(1);
        long handle = STBVorbis.stb_vorbis_open_memory(vorbis, errorBuffer, null);
        if (handle == NULL) {
            throw new RuntimeException("Failed to open Ogg Vorbis file. Error: " + errorBuffer.get(0));
        }
        return new OggData(handle);
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
        }
        ALC10.alcCloseDevice(device);
        ALC.destroy();
    }

    private static long openDefaultDevice() {
        long device = ALC10.alcOpenDevice((ByteBuffer) null);
        if (device == NULL) {
            throw new IllegalStateException("Failed to open the default device.");
        }
        return device;
    }
}
