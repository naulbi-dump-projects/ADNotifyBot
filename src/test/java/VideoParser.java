import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class VideoParser {

    public static void main(String[] args) {
        try {
            String videoUrl = "https://anidub.live/11814-.html";

            Document document = Jsoup.connect(videoUrl).get();
            Elements firstElement = document.body().select(".wrap")  // end popular
                    .select(".article.full2.ignore-select")
                    .select(".fright.fx-1")
                    .select(".flist");

            for (Element flistElement : firstElement) {
                // Проверьте, содержит ли элемент текст ".torrent"
                if (flistElement.text().contains(".torrent")) {
                    // Найдите ссылку внутри элемента
                    Element linkElement = flistElement.select("a").first();
                    String trackerLink = linkElement.attr("href");
                    System.out.println("Ссылка на трекер: " + trackerLink);
                    int idVideo = Integer.parseInt(trackerLink.split("-")[0].substring(trackerLink.lastIndexOf("/") + 1));
                    System.out.println(idVideo);
                    System.out.println("https://tr.anidub.com/" + idVideo + "-.html");
                }
            }
        }catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    public static void mainSibnetParser(String[] args) throws Exception {
        String seriesUrl = "https://video.sibnet.ru/shell.php?videoid=5278357";
        Document docSibnet = Jsoup.connect(seriesUrl).get();
        Elements videoElement = docSibnet.body().getAllElements();

        String htmlCode = videoElement.select("script").toString();

        // Регулярное выражение для поиска строки с "mp4"
        String regex = "\"(/v/[^\"']*\\.mp4)\"";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(htmlCode);

        while (matcher.find()) {
            String mp4Link = matcher.group(1);
            System.out.println("Найдена ссылка на MP4: " + mp4Link);
            System.out.println("https://video.sibnet.ru" + mp4Link);
        }


    }


    public static void mainAniDubSibnet(String[] args) {
        // https://anidub.live/11806-.html
        // video.sibnet.ru
        // div class -> video-js vjs-default-skin vjs-controls-enabled vjs-has-started vjs-paused vjs-user-inactive
        // video -> id video_html5_wrapper_html5_api & class vjs-tech, "src"

        int seriesNumber = 2;

        String websiteUrl = "https://anidub.live/11806-.html"; // Замените на URL вашего сайта
        try {
            Document doc = Jsoup.connect(websiteUrl).get();
            Elements owlItems = doc.body().getAllElements();
            Elements seriesElements = owlItems.select(".wrap")  // end popular
                            .select(".article.full2.ignore-select")
                            .select(".fright.fx-1")
                            .select(".fplayer.tabs-box")
                            .select(".tabs-b.video-box")
                            .get(1)
                            .select(".tabs-sel.series-tab span");
            // Проверка, что номер серии не превышает количество элементов
            if (seriesNumber >= 1 && seriesNumber <= seriesElements.size()) {
                // Извлечение URL из атрибута "data" элемента с соответствующим номером серии
                Element seriesElement = seriesElements.get(seriesNumber - 1);
                String seriesUrl = seriesElement.attr("data");

                System.out.println("URL для Серии " + seriesNumber + ": " + seriesUrl);

                Document docSibnet = Jsoup.connect(seriesUrl).get();
                Elements videoElement = docSibnet.body().getAllElements();
                System.out.println(videoElement);

                //String srcValue = videoElement.attr("src");

                //System.out.println("Значение атрибута src: " + srcValue);

            } else {
                System.out.println("Неверный номер серии.");
            }


            System.out.println("\n\n\n\n\n" + seriesElements);
            /*final List<Element> items = popular ?
                    new ArrayList<>(owlItems.select(".popular").select(".th-item"))
                    :
                    new ArrayList<>(owlItems.select(".content").select(".sect-content").select(".th-item"));
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
                }
            }
            System.out.println(videosToString(videos));*/
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
