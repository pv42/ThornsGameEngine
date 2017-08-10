package engine.audio;

import engine.toolbox.Log;
import org.lwjgl.BufferUtils;
import org.lwjgl.openal.AL10;
import org.joml.Vector3f;
import org.lwjgl.openal.SOFTDirectChannels;
import org.lwjgl.stb.STBVorbis;

import java.nio.IntBuffer;
import java.nio.ShortBuffer;


/**
 * Created by pv42 on 02.07.16.
 */
public class Source {
    private static final int BUFFER_SIZE = 1024 * 4;
    private int sourceId;
    private Vector3f position = new Vector3f();
    private IntBuffer buffers;
    private ShortBuffer pcm;
    private int samplesLeft;
    public Source() {
        sourceId = AL10.alGenSources();
        //AL10.alSourcef(sourceId,AL10.AL_GAIN,1);
        //AL10.alSourcef(sourceId,AL10.AL_PITCH,1);
        //AL10.alSource3f(sourceId, AL10.AL_POSITION,position.x(),position.y(),position.z());
        AL10.alSourcei(sourceId, SOFTDirectChannels.AL_DIRECT_CHANNELS_SOFT, AL10.AL_TRUE);

        buffers = BufferUtils.createIntBuffer(2);
        AL10.alGenBuffers(buffers);
        pcm = BufferUtils.createShortBuffer(BUFFER_SIZE);
    }
    public void play(int buffer) {
        AL10.alSourcei(sourceId,AL10.AL_BUFFER, buffer); //old
        AL10.alSourceQueueBuffers(sourceId, buffer); //new
        AL10.alSourcePlay(sourceId);

    }
    public boolean play(OggData oggData) {
        samplesLeft = oggData.getLengthSamples();
        for (int i = 0; i < buffers.limit(); i++) {
            if (!stream(buffers.get(i),oggData,pcm)) {
                return false;
            }
        }

        AL10.alSourceQueueBuffers(sourceId, buffers);
        AL10.alSourcePlay(sourceId);
        return true;
    }
    private boolean stream(int buffer, OggData oggData, ShortBuffer pcm) {
        int samples = 0;

        while (samples < BUFFER_SIZE) {
            pcm.position(samples);
            int samplesPerChannel = STBVorbis.stb_vorbis_get_samples_short_interleaved(oggData.getDecoder(), oggData.getChannels(), pcm);
            if (samplesPerChannel == 0) {
                break;
            }

            samples += samplesPerChannel * oggData.getChannels();
        }

        if (samples == 0) {
            return false;
        }

        pcm.position(0);
        AL10.alBufferData(buffer, oggData.getFormat(), pcm, oggData.getSampleRate());
        samplesLeft -= samples / oggData.getChannels();
        return true;
    }
    public boolean update( OggData oggData) {
        int processed = AL10.alGetSourcei(sourceId, AL10.AL_BUFFERS_PROCESSED);
        for (int i = 0; i < processed; i++) {
            int buffer = AL10.alSourceUnqueueBuffers(sourceId);

            if (!stream(buffer, oggData,pcm)) {
                return false;
            }
            AL10.alSourceQueueBuffers(sourceId, buffer);
        }

        if (processed == 2) {
            AL10.alSourcePlay(sourceId);
        }

        return true;
    }

    public void setPosition(Vector3f position) {
        this.position = position;
    }

    public void delete() {
        AL10.alDeleteSources(sourceId);
    }

    @Override
    protected void finalize() throws Throwable {
        AL10.alDeleteSources(sourceId);
        super.finalize();
    }
}
