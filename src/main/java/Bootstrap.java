import com.anidub.ADNotifyBot.BotLauncher;

public class Bootstrap {
    /**
     * Original code by https://github.com/Leymooo/BungeeCord/blob/master/bootstrap/src/main/java/net/md_5/bungee/Bootstrap.java
     * @param args
     */
    public static void main(String[] args) {
        if (Float.parseFloat(System.getProperty("java.class.version")) < 52.0) {
            System.err.println("This app need Java 8. Please check version usage command: java -version");
            return;
        }
        System.out.println("Process initializing message...");
        BotLauncher.main(args);
    }
}
