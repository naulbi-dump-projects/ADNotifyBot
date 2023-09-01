package com.anidub.ADNotifyBot;

import com.anidub.ADNotifyBot.database.DatabaseManager;
import com.anidub.ADNotifyBot.message.MessageHandler;
import com.anidub.ADNotifyBot.web.WebParser;
import flaticommunity.log.FlatiLogger;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static flaticommunity.log.TypeLogger.*;

public class BotLauncher {

    public static WebParser webParser;
    public static MessageHandler messageHandler;
    public static DatabaseManager databaseManager;

    private static final String TELEGRAM_BOT_TOKEN = "6664136994:AAGOllkVtSZ5FdknUqmH4R_PSsOo5eZC45Q";
    public static List<Integer> videosIds = new ArrayList<>();//Collections.synchronizedList(new ArrayList<>());
    public static ScheduledExecutorService executorService;
    public static FlatiLogger flatiLogger;

    public static void main(String[] args) {
        try {
            flatiLogger = new FlatiLogger();
            flatiLogger.log(INFO, "[Startup] Запуск &fботика&r...");

            flatiLogger.log(INFO, "[Startup] [&cHooks&r] Добавление &estop()&r...");
            Runtime.getRuntime().addShutdownHook(new Thread(BotLauncher::stop));

            flatiLogger.log(INFO, "[Startup] [&eDatabase&r] Подключение к &eбазе данных&r...");
            databaseManager = new DatabaseManager(
                    String.format(
                        "jdbc:mysql://%s:%d/%s?useSSL=%s",
                        "195.18.27.252", // host
                        (short) 3306, // port
                        "anidub_notify", // database
                        "false"
                    ),
                    "anidub_notify",
                    "AD_9jd59Qvdj5zVvD7z5nhJW59rr52ZZ7MsAzesY6hRATfnfZfugGxTRsS9XhVAWZmzskTCEgv63qfHjxaNKnTTNpHC6X");

            flatiLogger.log(INFO, "[Startup] [&bTelegram&r] Регистрация &bтелеграм &rбота...");
            registerTelegram();

            flatiLogger.log(INFO, "[Startup] [&dWebParser&r] Регистрация &dвеб-парсера&r...");
            webParser = new WebParser("https://anidub.live");

            flatiLogger.log(INFO, "[Startup] [&aExecutors&r] Регистрация &aschedulers&r...");
            executorService = Executors.newScheduledThreadPool(2);
            // database
            executorService.scheduleAtFixedRate(() -> {
                flatiLogger.log(INFO, "[&aExecutors&r] Исполнение &ascheduler &rдля &edatabase&r.");
                // Вызов метода выполнения запроса
                try {
                    final List<Integer> syncIdsVideos = databaseManager.executeQuery();
                    if (videosIds.isEmpty()) { // Первая загрузка
                        videosIds.addAll(syncIdsVideos);
                        return;
                    }

                    if (videosIds.equals(syncIdsVideos)) return; // Синхронизация каждые 5 минут
                    databaseManager.updateData();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }, 0, 5, TimeUnit.MINUTES); // 5 минут
            // site
            executorService.scheduleAtFixedRate(() -> {
                flatiLogger.log(INFO, "[&aExecutors&r] Исполнение &ascheduler &rдля &dвеб-парсинга&r.");
                try {
                    webParser.parsePopular();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }, 5, 60, TimeUnit.SECONDS); // 1 минута

            flatiLogger.log(INFO, "[Startup] Ботик &2запущен&r.");
        }catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private static void registerTelegram() throws Exception {
        // Код бота Telegram
        messageHandler = new MessageHandler(TELEGRAM_BOT_TOKEN);

        TelegramBotsApi telegramBotsApi = new TelegramBotsApi(DefaultBotSession.class);
        telegramBotsApi.registerBot(messageHandler);
    }

    public static void stop() {
        flatiLogger.log(INFO, "[Startup] &cОстановочка &fботика...");

        flatiLogger.log(INFO, "[Startup] Shutdown executors...");
        executorService.shutdown();

        try {
            Thread.sleep(500L);
        }catch (InterruptedException ex) {}

        flatiLogger.log(INFO, "[Startup] Update cached live results...");
        databaseManager.updateData();

        //flatiLogger.log(INFO, "[Startup] Shutdown database message...");
        //databaseManager.hikariDataSource.close();

        flatiLogger.log(INFO, "[Startup] Всё &2остановлено&r! Гудбай.");
    }

}
