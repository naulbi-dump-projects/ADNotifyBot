import org.jsoup.*;
import java.util.*;
import org.jsoup.nodes.*;
import java.util.regex.*;
import java.util.stream.*;
import org.jsoup.select.*;

public class AnimeIDsLiveParser {

    public static void main(String[] args) {
        Map<Integer, Integer> videos = new HashMap<>();
        String websiteUrl = "https://anidub.live"; // Замените на URL вашего сайта
        try {
            Document doc = Jsoup.connect(websiteUrl).get();
            Elements owlItems = doc.body().getAllElements();
            final List<Element> items = new ArrayList<>(owlItems.select(".popular").select(".th-item"));
            Collections.reverse(items);
            for (Element owlItem : items) {
                String title = owlItem.select(".th-title").text();
                String link = owlItem.select(".th-in").attr("href");
                String rating = owlItem.select(".th-rating").text();
                int idVideo = Integer.parseInt(link.split("-")[0].substring(link.lastIndexOf("/") + 1));
                int series = parseSeries(title);
                assert series != -1 : "int series is value -1";
                System.out.print("[ID:series] [" + idVideo + ":" + series + "]  ");
                if (!videos.containsKey(idVideo)) {
                    videos.put(idVideo, series);
                    System.out.println(title);
                    /*System.out.println(link);
                    System.out.println(rating);
                    System.out.println(owlItem.select(".th-img").select("img").attr("src"));
                */}
                //System.out.println("FOR|" + StringUtils.join(videos, ", "));
            }
            System.out.println(videosToString(videos));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private static final Pattern pattern = Pattern.compile("\\[(\\d+)[^X\\d]+([X\\d]+)\\]");
    public static Integer parseSeries(String title) {
        final Matcher matcher = pattern.matcher(title);
        return matcher.find() ? Integer.parseInt(matcher.group(1)) : -1;
    }

    public static String videosToString(Map<Integer, Integer> videos) {
        return videos.entrySet().stream()
                .map(entry -> entry.getKey() + ":" + entry.getValue())
                .collect(Collectors.joining(";"));
    }
}
