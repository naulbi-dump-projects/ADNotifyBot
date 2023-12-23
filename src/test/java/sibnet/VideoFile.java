package sibnet;

public class VideoFile {

    private String title;
    private String fileUrl;
    private String pageUrl;
    private String filename;
    private long size;
    private boolean prepared;

    public VideoFile(String pageUrl, String filename) {
        this.pageUrl = pageUrl;
        this.filename = filename;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getFileUrl() {
        return fileUrl;
    }

    public void setFileUrl(String fileUrl) {
        this.fileUrl = fileUrl;
    }

    public String getPageUrl() {
        return pageUrl;
    }

    public String getFilename() {
        return filename;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public boolean isPrepared() {
        return prepared;
    }

    public void setPrepared(boolean prepared) {
        this.prepared = prepared;
    }
}
