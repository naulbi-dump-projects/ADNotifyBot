package sibnet;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.concurrent.CompletableFuture;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class SibnetLoader extends Loader {

    private static final String HOST = "video.sibnet.ru";
    private static final String UA = "Mozilla/5.0 (Linux; Android 7.1.2; AFTMM Build/NS6265; wv) AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0 Chrome/70.0.3538.110 Mobile Safari/537.36";

    private static final String TITLE_REGEX = "videoName'>(.*)</h1>";
    private static final String URL_REGEX = "/v/.+\\d+.mp4";
    private static final String EXT_REGEX = "\\d+(\\.\\w+)\\?";

    private OkHttpClient client;

    public SibnetLoader(OkHttpClient client) {
        this.client = client;
    }

    @Override
    public CompletableFuture<Long> prepare(VideoFile videoFile) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                enrichVideoFile(videoFile);
                return videoFile.getSize();
            } catch (IOException e) {
                throw new RuntimeException("Failed to prepare video file.", e);
            }
        });
    }

    private void enrichVideoFile(VideoFile videoFile) throws IOException {
        Request request = new Request.Builder()
                .url(videoFile.getPageUrl())
                .header("User-Agent", UA)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Failed to fetch video page. HTTP status code: " + response.code());
            }

            String responseBody = response.body().string();

            Matcher titleMatcher = Pattern.compile(TITLE_REGEX).matcher(responseBody);
            if (titleMatcher.find()) {
                videoFile.setTitle(titleMatcher.group(1));
            }

            Matcher urlMatcher = Pattern.compile(URL_REGEX).matcher(responseBody);
            if (urlMatcher.find()) {
                videoFile.setFileUrl(HOST + urlMatcher.group(0));
            }
        }
    }

    @Override
    public CompletableFuture<Void> proceedVideo(VideoFile video) {
        return CompletableFuture.runAsync(() -> {
            try {
                createFile(video.getFilename(), video.getSize());
                download(video);
            } catch (Loader.MemoryError e) {
                System.out.println("\"" + video.getTitle() + "\" can't be proceeded: " + e.getMessage());
            } catch (Exception e) {
                e.printStackTrace();
                deleteFile(video.getFilename());
            }
        });
    }

    @Override
    public void createFile(String filename, long size) throws Loader.MemoryError {
        // Implement logic to create a file
    }

    @Override
    public void deleteFile(String filename) {
        // Implement logic to delete a file
    }

    @Override
    public CompletableFuture<Void> download(VideoFile video) {
        return CompletableFuture.runAsync(() -> {
            try {
                downloadVideoParts(video);
            } catch (IOException e) {
                throw new RuntimeException("Failed to download video parts.", e);
            }
        });
    }

    private void downloadVideoParts(VideoFile video) throws IOException {
        FileSink fileSink = new FileSink("X:/test/video.mp4", video.getSize());
        CompletableFuture<Void>[] downloadFutures = new CompletableFuture[HANDLERS];

        for (int i = 0; i < HANDLERS; i++) {
            int start = (int) (i * (video.getSize() / HANDLERS));
            int end = (int) ((i + 1) * (video.getSize() / HANDLERS) - 1);

            downloadFutures[i] = CompletableFuture.runAsync(() -> {
                try {
                    downloadPart(video.getFileUrl(), start, end, fileSink);
                } catch (IOException e) {
                    throw new RuntimeException("Failed to download video part.", e);
                }
            });
        }

        CompletableFuture.allOf(downloadFutures).join();
    }

    @Override
    public void downloadPart(String url, int start, int end, FileSink fileSink) throws IOException {
        Request request = new Request.Builder()
                .url(url)
                .header("Range", "bytes=" + start + "-" + end)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Failed to download video part. HTTP status code: " + response.code());
            }

            ResponseBody body = response.body();
            if (body != null) {
                ByteBuffer buffer = ByteBuffer.wrap(body.bytes());
                fileSink.writeData(buffer);
            }
        }
    }



}
