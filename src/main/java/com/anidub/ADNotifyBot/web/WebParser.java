package com.anidub.ADNotifyBot.web;

import com.anidub.ADNotifyBot.*;
import lombok.*;
import org.jsoup.*;
import org.jsoup.nodes.*;
import org.jsoup.select.*;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static flaticommunity.log.TypeLogger.*;

@AllArgsConstructor(access = AccessLevel.PUBLIC)
public class WebParser {
    public String websiteUrl;

    public void parsePopular() throws Exception {
        //BotLauncher.flatiLogger.log(INFO, "Synced VideosIDs list: " + videosToString(BotLauncher.videos));

        Document doc = Jsoup.connect(websiteUrl).get();
        Elements owlItems = doc.body().getAllElements();
        int newVideos = 1;
        final List<Element> items = new ArrayList<>(owlItems.select(".popular").select(".th-item"));
        Collections.reverse(items);
        Map<Integer, Integer> parsedVideos = new HashMap<>();
        for (Element owlItem : items) {
            String title = owlItem.select(".th-title").text();
            String link = owlItem.select(".th-in").attr("href");
            int idVideo;
            try {
                idVideo = Integer.parseInt((link.split("-")[0].substring((link.lastIndexOf("/") + 1))));
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
                BotLauncher.messageHandler.sendImage("-1001910022940",
                        "<b>" + title + "</b>\n\n"
                                + "Зрительский рейтинг: " + rating + "\n\n"
                                + "Открыть сайт: \n- <a href=\"anidub.com/" + idVideo + "-.html\">Зеркало anidub.com</a>\n"
                                + "- <a href=\"anidub.live/" + idVideo + "-.html\">Зеркало anidub.live</a>\n"
                                + "- <a href=\"anidub.life/" + idVideo + "-.html\">Зеркало anidub.life</a>\n"
                                + "- <a href=\"anidub.club/" + idVideo + "-.html\">Зеркало anidub.club</a>\n"
                                + "\nt.me/anidubnotify | t.me/anidubnotifydev\n"
                                + "\n#video" + idVideo
                        // + "Открыть сайт: <a href=\"anidub.vip/" + idVideo  +"-.html\">Зеркало anidub.vip</a>"
                        // + MessageFormat.format(domain, "anidub.live", idVideo) + "\n" +
                        // MessageFormat.format(domain, "anidub.vip", idVideo)
                        ,
                        "https://anidub.live" + owlItem.select(".th-img").select("img").attr("src")
                );
                Thread.sleep(5000L * newVideos++);
            }
            //BotLauncher.flatiLogger.log(INFO, "FOR|" + videosToString(parsedVideos));
        }

        BotLauncher.videos = parsedVideos;

        //BotLauncher.flatiLogger.log(INFO, "Videos list updated: " + videosToString(BotLauncher.videos));
    }


    private static final Pattern pattern = Pattern.compile("\\[(\\d+) из ([\\w\\d]+)\\]$");
    public static Integer parseSeries(String title) {
        final Matcher matcher = pattern.matcher(title.toLowerCase()); // зачем стоит lowerCase? - многоуважаемые регулярные выражения не понимают CASE_INSENSITIVE
        return matcher.find() ? Integer.parseInt(matcher.group(1)) : -1;
    }

    /*public String videosToString(Map<Integer, Integer> videos) {
        return videos.entrySet().stream()
                .map(entry -> entry.getKey() + ":" + entry.getValue())
                .collect(Collectors.joining(", "));
    }*/
}
