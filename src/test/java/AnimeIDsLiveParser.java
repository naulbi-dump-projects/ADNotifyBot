import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AnimeIDsLiveParser {
    public static void main(String[] args) {
        List<Integer> idsVideos = new ArrayList<>();
        String websiteUrl = "https://anidub.live"; // Замените на URL вашего сайта
        try {
            Document doc = Jsoup.connect(websiteUrl).get();
            Elements owlItems = doc.body().getAllElements();
            int i = 0;
            final List<Element> items = new ArrayList<>(owlItems.select(".popular").select(".th-item"));
            Collections.reverse(items);
            for (Element owlItem : items) {
                //System.out.println(i++);
                String title = owlItem.select(".th-title").text();
                String link = owlItem.select(".th-in").attr("href");
                String rating = owlItem.select(".th-rating").text();
                int idVideo = Integer.parseInt(link.split("-")[0].substring(link.lastIndexOf("/") + 1));
                //System.out.println("ID: " + idVideo);
                if (!idsVideos.contains(idVideo)) {
                    idsVideos.add(idVideo);
                    //System.out.println(title);
                    //System.out.println(link);
                    //System.out.println(rating);
                   // System.out.println(owlItem.select(".th-img").select("img").attr("src"));
                }
                //System.out.println("FOR|" + StringUtils.join(idsVideos, ", "));
            }
            System.out.println(StringUtils.join(idsVideos, ","));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
