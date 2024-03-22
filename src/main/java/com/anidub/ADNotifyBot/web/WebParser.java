package com.anidub.ADNotifyBot.web;

import com.anidub.ADNotifyBot.*;
import lombok.*;
import org.jsoup.*;
import org.jsoup.nodes.*;
import org.jsoup.select.*;

import java.util.*;
import java.util.regex.*;
import java.util.stream.Collectors;

import static flaticommunity.log.TypeLogger.*;

@AllArgsConstructor(access = AccessLevel.PUBLIC)
public class WebParser {
    public String websiteUrl;

    public void parsePopular() throws Exception {
        BotLauncher.flatiLogger.log(INFO, "Synced VideosIDs list: " + videosToString(BotLauncher.videos));

        Document doc = Jsoup.connect(websiteUrl).get();
        Elements owlItems = doc.body().getAllElements();
        int newVideos = 1;
        //final List<Element> items = new ArrayList<>(owlItems.select(".popular").select(".th-item"));
        final List<Element> items = new ArrayList<>(owlItems.select(".content").select(".sect-content").select(".th-item"));
        Collections.reverse(items);
        Map<Integer, Integer> parsedVideos = new HashMap<>();
        for (Element owlItem : items) {
            String title = owlItem.select(".th-title").text();
            String link = owlItem.select(".th-in").attr("href");
            int idVideo;
            try {
                idVideo = Integer.parseInt(parseIdVideo(link));
                if (idVideo < 0 || idVideo >= Integer.MAX_VALUE) continue;
            } catch (NumberFormatException ex) {
                ex.printStackTrace();
                continue;
            }
            //BotLauncher.flatiLogger.log(INFO, "ID: " + idVideo);

            String rating = owlItem.select(".th-rating").text();
            int series = parseSeries(title);
            assert series != -1 : "int series is value -1";

            //BotLauncher.flatiLogger.log(INFO, "Series: " + series);
            parsedVideos.put(idVideo, series);

            if (!BotLauncher.videos.containsKey(idVideo) || !BotLauncher.videos.get(idVideo).equals(series)) {
                //BotLauncher.flatiLogger.log(INFO, title + "\n" + link + "\n" + rating + "\n" + owlItem.select(".th-img").select("img").attr("src"));
                BotLauncher.flatiLogger.log(INFO, "[NEW VIDEO/" + idVideo + "] " + title);

                String trackerURL = parseTracker("https://anidub.pro/" + idVideo + "-.html");

                StringBuilder messageBuilder = new StringBuilder();
                messageBuilder.append("<b>").append(title).append("</b>\n\n");
                messageBuilder.append("Текущая серия: ").append(series).append("\n");
                messageBuilder.append("Зрительский рейтинг: ").append(rating).append("\n");
                //+ "Тип получения данных: <b>Со страницы</b>/<strike>С ленты</strike>\n"
                messageBuilder.append("\nОткрыть сайт: \n");
                messageBuilder.append("- <a href=\"anidub.com/").append(idVideo).append("-.html\">Зеркало anidub.com</a>\n");
                messageBuilder.append("- <a href=\"anidub.pro/").append(idVideo).append("-.html\">Зеркало anidub.pro</a>\n");
                messageBuilder.append("- <a href=\"anidub.live/").append(idVideo).append("-.html\">Зеркало anidub.live</a>\n");
                messageBuilder.append("- <a href=\"anidub.life/").append(idVideo).append("-.html\">Зеркало anidub.life</a>\n");
                messageBuilder.append("- <a href=\"anidub.club/").append(idVideo).append("-.html\">Зеркало anidub.club</a>\n");
                if(trackerURL != null) {
                    messageBuilder.append("Открыть трекер: \n");
                    messageBuilder.append("- <a href=\"").append(trackerURL).append("\">Зеркало anidub.com</a>\n");
                }
                messageBuilder.append("\nt.me/anidubnotify | t.me/anidubnotifydev\n");
                messageBuilder.append("\n#video").append(idVideo);
                // + "Открыть сайт: <a href=\"anidub.vip/" + idVideo  +"-.html\">Зеркало anidub.vip</a>"
                // + MessageFormat.format(domain, "anidub.live", idVideo) + "\n" +
                // MessageFormat.format(domain, "anidub.vip", idVideo)

                BotLauncher.messageHandler.sendImage("-1002088760542", // -1002088760542 = main channel || -1002091080112 = dev channel
                        messageBuilder.toString(),
                        "https://anidub.pro" + owlItem.select(".th-img").select("img").attr("src")
                );
                Thread.sleep(5000L * newVideos++);
            }

            //BotLauncher.flatiLogger.log(INFO, "FOR|" + videosToString(parsedVideos));
        }
        BotLauncher.videos = parsedVideos; // да, каждый раз обновлять список плохая затея, но если стопать бота во время парсинга, то будет ещё хуже /:

        BotLauncher.flatiLogger.log(INFO, "Videos list updated: " + videosToString(BotLauncher.videos));
    }

    public String parseTracker(String videoUrl) throws Exception {
        Document document = Jsoup.connect(videoUrl).get();
        Elements flistElements = document.body().select(".wrap")  // end popular
                .select(".article.full2.ignore-select")
                .select(".fright.fx-1")
                .select(".flist");

        for (Element flistElement : flistElements) {
            // Проверьте, содержит ли элемент текст ".torrent"
            if (flistElement.text().contains(".torrent")) {
                // Найдите ссылку внутри элемента
                Element linkElement = flistElement.select("a").first();
                String trackerLink = linkElement.attr("href");
                System.out.println("Ссылка на трекер: " + trackerLink);

                return "tr.anidub.com/" + parseIdVideo(trackerLink) + "-.html";
            }
        }
        return null;
    }


    private static final Pattern pattern = Pattern.compile("\\[(\\d+)[^X\\d]+([X\\d]+)\\]");
    public static Integer parseSeries(String title) {
        final Matcher matcher = pattern.matcher(title);
        return matcher.find() ? Integer.parseInt(matcher.group(1)) : -1;
    }

    public static String parseIdVideo(String link) {
        return (link.split("-")[0].substring((link.lastIndexOf("/") + 1)));
    }

    public String videosToString(Map<Integer, Integer> videos) {
        return videos.entrySet().stream()
                .map(entry -> entry.getKey() + ":" + entry.getValue())
                .collect(Collectors.joining(", "));
    }
}
