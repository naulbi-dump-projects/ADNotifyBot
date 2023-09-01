package com.anidub.ADNotifyBot.web;

import com.anidub.ADNotifyBot.*;
import lombok.*;
import org.apache.commons.lang3.*;
import org.jsoup.*;
import org.jsoup.nodes.*;
import org.jsoup.select.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static flaticommunity.log.TypeLogger.*;

@AllArgsConstructor(access = AccessLevel.PUBLIC)
public class WebParser {
    public String websiteUrl;

    public void parsePopular() throws Exception {
        BotLauncher.flatiLogger.log(INFO, "Synced VideosIDs list: " + StringUtils.join(BotLauncher.videosIds, ","));

        Document doc = Jsoup.connect(websiteUrl).get();
        Elements owlItems = doc.body().getAllElements();
        int newVideos = 1;
        final List<Element> items = new ArrayList<>(owlItems.select(".popular").select(".th-item"));
        Collections.reverse(items);
        for (Element owlItem : items) {
            String title = owlItem.select(".th-title").text();
            String link = owlItem.select(".th-in").attr("href");
            int idVideo;
            try {
                idVideo = Integer.parseInt((link.split("-")[0].substring((link.lastIndexOf("/") + 1))));
                if (idVideo < 0 || idVideo >= Integer.MAX_VALUE) continue;
            } catch (NumberFormatException ex) {
                continue;
            }
            BotLauncher.flatiLogger.log(INFO, "ID: " + idVideo);


            if (!BotLauncher.videosIds.contains(idVideo)) {
                String rating = owlItem.select(".th-rating").text();
                BotLauncher.videosIds.add(idVideo);

                BotLauncher.flatiLogger.log(INFO, title + "\n" + link + "\n" + rating + "\n" + owlItem.select(".th-img").select("img").attr("src"));
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
            BotLauncher.flatiLogger.log(INFO, "FOR|" + StringUtils.join(BotLauncher.videosIds, ", "));
        }

        BotLauncher.flatiLogger.log(INFO, "VideoIDs list updated: " + StringUtils.join(BotLauncher.videosIds, ","));
    }
}
