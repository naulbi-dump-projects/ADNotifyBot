import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

public class Test {
    public static void main(String[] args) {
        MessageHandler messageHandler = new MessageHandler("");

        TelegramBotsApi telegramBotsApi = null;
        try {
            telegramBotsApi = new TelegramBotsApi(DefaultBotSession.class);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
        try {
            telegramBotsApi.registerBot(messageHandler);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }

        try {
            messageHandler.sendVideo("", "#grizzyleslemmings #grizzyandlemmings #grizzylemmings", "https://s64.123apps.com/vcutter/d/s64s72eiUGuztK_mp4_T7e85itW.mp4");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main2(String[] args) {
        String test = "1l";
        System.out.println(Integer.parseInt(test));
    }

}
