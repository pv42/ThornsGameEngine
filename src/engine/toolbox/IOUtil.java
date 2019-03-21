package engine.toolbox;

import org.lwjgl.BufferUtils;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.SeekableByteChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.lwjgl.BufferUtils.createByteBuffer;

public class IOUtil {
    private static final String TAG = "IOUtil";

    public static ByteBuffer ioResourceToByteBuffer(String resource, int bufferSize) throws IOException {
        ByteBuffer buffer;

        Path path = Paths.get(resource);
        if (Files.isReadable(path)) {
            try (SeekableByteChannel fc = Files.newByteChannel(path)) {
                buffer = createByteBuffer((int) fc.size() + 1);
                while (fc.read(buffer) != -1);
            }
        } else {
            try (
                    InputStream source = IOUtil.class.getClassLoader().getResourceAsStream(resource);
                    ReadableByteChannel rbc = Channels.newChannel(source)
            ) {
                buffer = createByteBuffer(bufferSize);

                while (true) {
                    int bytes = rbc.read(buffer);
                    if (bytes == -1) {
                        break;
                    }
                    if (buffer.remaining() == 0) {
                        buffer = resizeBuffer(buffer, buffer.capacity() * 2);
                    }
                }
            }
        }
        buffer.flip();
        return buffer;
    }
    private static ByteBuffer resizeBuffer(ByteBuffer buffer, int newCapacity) {
        ByteBuffer newBuffer = BufferUtils.createByteBuffer(newCapacity);
        buffer.flip();
        newBuffer.put(buffer);
        return newBuffer;
    }

    public static InputStream loadFileAsStream(String filename) throws FileNotFoundException {
        /*if(!inJarInit) {
            inJarInit = true;
            new FileLoader();
        }*/
        Log.d(TAG, "loading \"" +  filename + "\"");
        if (/*inJar*/ false) {
            return ClassLoader.getSystemResourceAsStream(filename);
        } else {
            if(new File(filename).exists()) return new FileInputStream(filename);
            return new FileInputStream(filename);
        }
    }


    public static ByteBuffer loadFileAsBuffer(String filename) throws IOException {
        BufferedInputStream in = new BufferedInputStream(loadFileAsStream(filename),4096);
        ByteBuffer buffer = ByteBuffer.allocateDirect(4096);
        int i;
        while ( true) {
            i = in.read();
            if(i == -1) break;
            buffer.put((byte) i);
            if(buffer.remaining() == 0) {
                ByteBuffer newBuffer = ByteBuffer.allocateDirect(buffer.capacity() * 2);
                buffer.flip();
                newBuffer.put(buffer);
                buffer =  newBuffer;
            }
        }
        buffer.flip();
        return buffer;
    }

}
