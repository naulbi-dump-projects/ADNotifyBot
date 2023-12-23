package sibnet;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SibnetVideo extends VideoFile {

    private static final Pattern EXT_REGEX = Pattern.compile("\\d+(\\.\\w+)\\?");

    public SibnetVideo(String pageUrl, String filename) {
        super(pageUrl, filename);
    }

    public String getExt() {
        Matcher matcher = EXT_REGEX.matcher(getFileUrl());
        if (matcher.find()) {
            return matcher.group(1);
        } else {
            throw new IllegalStateException("Extension not found in the file URL.");
        }
    }
}
