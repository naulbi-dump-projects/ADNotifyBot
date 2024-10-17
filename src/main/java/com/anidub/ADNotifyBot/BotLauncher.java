package com.anidub.ADNotifyBot;

import java.util.*;
import flaticommunity.log.*;
import java.util.concurrent.*;
import com.anidub.ADNotifyBot.web.*;
import com.anidub.ADNotifyBot.message.*;
import com.anidub.ADNotifyBot.database.*;
import static flaticommunity.log.TypeLogger.*;
import org.telegram.telegrambots.longpolling.*;
import org.telegram.telegrambots.client.okhttp.*;

public class BotLauncher {

    public static WebParser webParser;
    public static FlatiLogger flatiLogger;
    public static MessageHandler messageHandler;
    public static DatabaseManager databaseManager;
    public static ScheduledExecutorService executorService;

    public static Map<Integer, Integer> videos = new HashMap<>(); // idVideo, series
    //public static Map<Integer, Integer> trackerVideos = new HashMap<>(); // idVideo, trackerIdVideo

    private static final String TELEGRAM_BOT_TOKEN = "7101034406:AAH58bBLcg2G1W63PeIWTpbORv7iiAsFq4A";

    public static void main(String[] args) {
        try {
            flatiLogger = new FlatiLogger();
            flatiLogger.log(INFO, "[Startup] Запуск &fботика&r...");

            flatiLogger.log(INFO, "[Startup] [&cHooks&r] Добавление &estop()&r...");
            Runtime.getRuntime().addShutdownHook(new Thread(BotLauncher::stop));

            flatiLogger.log(INFO, "[Startup] [&eDatabase&r] Подключение к &eбазе данных&r...");
            databaseManager = (System.getProperty("anidubnotify.host") != null) ?
                    new DatabaseManager(
                        String.format(
                                "jdbc:mysql://%s:%d/%s?useSSL=%s",
                                System.getProperty("anidubnotify.host"),
                                (short) Integer.parseInt(System.getProperty("anidubnotify.port")),
                                System.getProperty("anidubnotify.database"),
                                System.getProperty("anidubnotify.useSSL")
                        ),
                        System.getProperty("anidubnotify.username"),
                        System.getProperty("anidubnotify.password"))
                    :
                    new DatabaseManager(
                        String.format(
                                "jdbc:mysql://%s:%d/%s?useSSL=%s",
                                "127.0.0.1", // host
                                (short) 3306, // port
                                "anidubnotify", // database
                                "false"
                        ),
                        "root",
                        "");

            flatiLogger.log(INFO, "[Startup] [&bTelegram&r] Регистрация &bтелеграм &rбота...");
            registerTelegram();

            flatiLogger.log(INFO, "[Startup] [&dWebParser&r] Регистрация &dвеб-парсера&r...");
            webParser = new WebParser("https://anidub.pro");

            flatiLogger.log(INFO, "[Startup] [&aExecutors&r] Регистрация &aschedulers&r...");
            executorService = Executors.newScheduledThreadPool(2);

            // database
            executorService.scheduleAtFixedRate(() -> {
                flatiLogger.log(INFO, "[&aExecutors&r] Процесс использования &ascheduler &rдля &edatabase&r.");

                final Map<Integer, Integer> syncVideos = databaseManager.executeQuery();
                if (videos.isEmpty()) { // Первая загрузка
                    videos.putAll(syncVideos);
                    flatiLogger.log(INFO, "[&aExecutors&r] &9Успешно &fиспользован &ascheduler &rдля &edatabase&r в ходе &4наполнения&r данными.");
                    return;
                }

                if (videos.equals(syncVideos)) {
                    flatiLogger.log(INFO, "[&aExecutors&r] &9Успешно &fиспользован &ascheduler &rдля &edatabase&r в качестве &6синхронизациии&r.");
                    return; // Синхронизация каждые 5 минут
                }

                databaseManager.updateData();

                flatiLogger.log(INFO, "[&aExecutors&r] &9Успешно &fиспользован &ascheduler &rдля &edatabase&r с новыми данными с &dвеб-парсинга&r.");
            }, 0, 5, TimeUnit.MINUTES); // 5 минут
            // site
            executorService.scheduleAtFixedRate(() -> {
                flatiLogger.log(INFO, "[&aExecutors&r] Процесс использования &ascheduler &rдля &dвеб-парсинга&r.");

                if(videos.isEmpty()) {
                    flatiLogger.log(INFO, "[&aExecutors&r] &cОтложено &fиспользование &ascheduler &rдля &dвеб-парсинга&r из-за загрузки &edatabase&r.");
                    return;
                }

                try {
                    webParser.parsePopular();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }

                flatiLogger.log(INFO, "[&aExecutors&r] &9Успешно &fиспользован &ascheduler &rдля &dвеб-парсинга&r.");
            }, 5, 60, TimeUnit.SECONDS); // 1 минута

            flatiLogger.log(INFO, "[Startup] Ботик &2запущен&r.");
        }catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private static void registerTelegram() {
        try (final TelegramBotsLongPollingApplication botsApplication = new TelegramBotsLongPollingApplication()) {
            botsApplication.registerBot(TELEGRAM_BOT_TOKEN, messageHandler = new MessageHandler(new OkHttpTelegramClient(TELEGRAM_BOT_TOKEN)));
            //Thread.currentThread().join();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void stop() {
        flatiLogger.log(INFO, "[Startup] &cОстановочка &fботика...");

        flatiLogger.log(INFO, "[Startup] Shutdown executors...");
        executorService.shutdown();

        try {
            Thread.sleep(500L);
        }catch (InterruptedException ignored) {}

        flatiLogger.log(INFO, "[Startup] Update cached live results...");
        databaseManager.updateData();

        flatiLogger.log(INFO, "[Startup] Shutdown database message...");
        databaseManager.hikariDataSource.close();

        flatiLogger.log(INFO, "[Startup] Всё &2остановлено&r! Гудбай.");
    }

}
