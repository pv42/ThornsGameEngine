package engine.audio;

import org.lwjgl.openal.ALC10;

import java.nio.ByteBuffer;

import static org.lwjgl.system.MemoryUtil.NULL;

public class Device {
    private long id;

    private Device(long id) {
        this.id = id;
    }

    static Device openDefaultDevice() {
        long id;
        try {
            id = ALC10.alcOpenDevice((ByteBuffer) null);
        } catch (NullPointerException ex) {
            throw new IllegalStateException("Failed to open the default device.");
        }
        //long device = ALC10.alcOpenDevice("");
        if (id == NULL) {
            throw new IllegalStateException("Failed to open the default device.");
        }
        return new Device(id);
    }

    protected long getId() {
        return id;
    }

    protected void close() {
        ALC10.alcCloseDevice(id);
    }
}
