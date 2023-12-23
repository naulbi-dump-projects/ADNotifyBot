package sibnet;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;

public abstract class Loader {

    protected static final int HANDLERS = 4;  // Adjust based on your requirements

    public abstract CompletableFuture<Long> prepare(VideoFile videoFile);

    public abstract CompletableFuture<Void> proceedVideo(VideoFile video);

    public abstract CompletableFuture<Void> download(VideoFile video);

    public abstract void createFile(String filename, long size) throws MemoryError;

    public abstract void deleteFile(String filename);

    public abstract void downloadPart(String url, int start, int end, FileSink sink) throws IOException;

    public static class MemoryError extends Exception {
        public MemoryError(String message) {
            super(message);
        }
    }
}
