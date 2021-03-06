package engine.audio;

import org.lwjgl.openal.AL10;
import org.lwjgl.stb.STBVorbis;
import org.lwjgl.stb.STBVorbisInfo;

/**
 * Created by pv42 on 10.07.2017.
 */
public class OggData {
    private STBVorbisInfo info;
    private int format;
    private int channels;
    private int sampleRate;
    private long decoder;
    private int lengthSamples;
    private float lengthSeconds;
    public OggData(long decoder) {
        this.decoder = decoder;
        try (STBVorbisInfo info = STBVorbisInfo.malloc()) {
            STBVorbis.stb_vorbis_get_info(decoder, info);
            channels = info.channels();
            sampleRate = info.sample_rate();

        }
        lengthSamples = STBVorbis.stb_vorbis_stream_length_in_samples(decoder);
        lengthSeconds = STBVorbis.stb_vorbis_stream_length_in_seconds(decoder);
        format = getFormat(channels);
    }
    public void printInfo() {
        System.out.println("channels = " + info.channels());
        System.out.println("sampleRate = " + sampleRate);
        System.out.println("maxFrameSize = " + info.max_frame_size());
        System.out.println("setupMemoryRequired = " + info.setup_memory_required());
        System.out.println("setupTempMemoryRequired() = " + info.setup_temp_memory_required());
        System.out.println("tempMemoryRequired = " + info.temp_memory_required());

    }
    private int getFormat(int channels) {
        switch (channels) {
            case 1:
                return AL10.AL_FORMAT_MONO16;
            case 2:
                return AL10.AL_FORMAT_STEREO16;
            default:
                throw new UnsupportedOperationException("Unsupported number of channels: " + channels);
        }
    }

    public int getFormat() {
        return format;
    }

    public int getSampleRate() {
        return sampleRate;
    }

    public long getDecoder() {
        return decoder;
    }

    public int getChannels() {
        return channels;
    }

    public int getLengthSamples() {
        return lengthSamples;
    }

    public float getLengthSeconds() {
        return lengthSeconds;
    }
}
