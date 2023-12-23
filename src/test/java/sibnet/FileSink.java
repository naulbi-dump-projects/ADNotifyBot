package sibnet;

import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

public class FileSink {

    private String path;
    private long size;

    public FileSink(String path, long size) {
        this.path = path;
        this.size = size;
    }

    public void writeData(ByteBuffer chunk) {
        try (RandomAccessFile file = new RandomAccessFile(path, "rwd");
             FileChannel channel = file.getChannel()) {

            channel.position(chunk.getLong(0));
            channel.write(chunk.slice());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
