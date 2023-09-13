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

    private static final String TELEGRAM_BOT_TOKEN = "6664136994:AAFD52IpWsVYhrcV3S4cixd_eOS0tJrFjRk";

    public static WebParser webParser;
    public static FlatiLogger flatiLogger;
    public static MessageHandler messageHandler;
    public static DatabaseManager databaseManager;
    public static ScheduledExecutorService executorService;

    public static Map<Integer, Integer> videos = new HashMap<>();//Collections.synchronizedList(new ArrayList<>());

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
            webParser = new WebParser("https://anidub.live");

            flatiLogger.log(INFO, "[Startup] [&aExecutors&r] Регистрация &aschedulers&r...");
            executorService = Executors.newScheduledThreadPool(2);
            // database
            executorService.scheduleAtFixedRate(() -> {
                flatiLogger.log(INFO, "[&aExecutors&r] Процесс использования &ascheduler &rдля &edatabase&r.");
                // Вызов метода выполнения запроса
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

        flatiLogger.log(INFO, "[Startup] Shutdown database message...");
        databaseManager.hikariDataSource.close();

        flatiLogger.log(INFO, "[Startup] Всё &2остановлено&r! Гудбай.");
    }

}
