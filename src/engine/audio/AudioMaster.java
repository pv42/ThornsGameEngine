package engine.audio;

import engine.toolbox.IOUtil;
import engine.toolbox.Log;
import org.joml.Vector3f;
import org.lwjgl.BufferUtils;
import org.lwjgl.openal.AL;
import org.lwjgl.openal.AL10;
import org.lwjgl.openal.ALC;
import org.lwjgl.openal.ALC10;
import org.lwjgl.openal.EXTThreadLocalContext;
import org.lwjgl.stb.STBVorbis;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.system.MemoryUtil.NULL;

/**
 * Created by pv42 on 02.07.16.
 */
public class AudioMaster {
    private static final String TAG = "Engine:AudioMaster";
    private static List<Integer> buffers = new ArrayList<>();
    private static long context;
    private static Device device;

    public static void init() {
        Log.i(TAG, "initialising");
        try {
            try {
                ALC.getFunctionProvider(); //test if OpenAL is initialized
                device = Device.openDefaultDevice();

            } catch (IllegalStateException ex) {
                ALC.create();
                Log.i(TAG, "reinitialized OpenAL");
                device = Device.openDefaultDevice();

            }
            //ALCCapabilities alcCapabilities = ALC.createCapabilities(device);
            context = ALC10.alcCreateContext(device.getId(), (IntBuffer) null);
            if (context == NULL) {
                throw new IllegalStateException("Failed to create an OpenAL context.");
            }
            EXTThreadLocalContext.alcSetThreadContext(context);
            AL.createCapabilities(ALC.createCapabilities(device.getId()));

        } catch (Exception e) {
            Log.e(TAG, "could not initialize audio");
            e.printStackTrace();
        }
    }


    public static OggData loadSound(String filename) {
        Log.d(TAG, "loading sound \"" + filename + "\"");
        ByteBuffer vorbis = null;
        try {
            vorbis = IOUtil.ioResourceToByteBuffer(filename, 256 * 1024);
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
        device.close();
        ALC10.alcDestroyContext(context);

        EXTThreadLocalContext.alcSetThreadContext(0);
        ALC.destroy();
    }
}
