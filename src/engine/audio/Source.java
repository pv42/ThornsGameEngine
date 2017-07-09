package engine.audio;

import org.lwjgl.BufferUtils;
import org.lwjgl.openal.AL10;
import org.joml.Vector3f;
import org.lwjgl.openal.SOFTDirectChannels;

import java.nio.IntBuffer;

/**
 * Created by pv42 on 02.07.16.
 */
public class Source {
    private int sourceId;
    private Vector3f position = new Vector3f();
    public Source() {
        sourceId = AL10.alGenSources();
        AL10.alSourcef(sourceId,AL10.AL_GAIN,1);
        AL10.alSourcef(sourceId,AL10.AL_PITCH,1);
        AL10.alSource3f(sourceId, AL10.AL_POSITION,position.x(),position.y(),position.z());
        AL10.alSourcei(sourceId, SOFTDirectChannels.AL_DIRECT_CHANNELS_SOFT, AL10.AL_TRUE);

        IntBuffer buffers = BufferUtils.createIntBuffer(2);
        //todo alGenBuffers(buffers);
    }
    public void play(int buffer) {
        AL10.alSourcei(sourceId,AL10.AL_BUFFER, buffer);
        AL10.alSourcePlay(sourceId);
    }

    public void setPosition(Vector3f position) {
        this.position = position;
    }

    public void delete() {
        AL10.alDeleteSources(sourceId);
    }
}
