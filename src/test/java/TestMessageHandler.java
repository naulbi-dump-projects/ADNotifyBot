public class TestMessageHandler /* extends TelegramLongPollingBot*/ { /*
    public String token;

    public TestMessageHandler(String token) {
        this.token = token;
    }

    @Override
    public void onUpdateReceived(Update update) {
        if(update.hasMessage() && update.getMessage().isUserMessage()) {
            sendTextMessage(update.getMessage().getChatId() + "", "Текущая версия бота непозволяет использовать его в личных сообщениях.\n\nНаш основной канал: @anidubnotify");
        }
    }

    public void sendImage(String chatId, String text, String url) {
        SendPhoto message = new SendPhoto();
        message.setChatId(chatId);
        message.setCaption(text);
        message.setParseMode("HTML");
        message.setPhoto(new InputFile(url));
        try {
            execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    public void sendVideo(String chatId, String text, String url) throws Exception {
        SendVideo message = new SendVideo();
        message.setChatId(chatId);
        message.setCaption(text);
        message.setParseMode("HTML");
        HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
        InputFile inputFile = new InputFile(connection.getInputStream(), "Grizzy découvre la console de jeux vidéo - Grizzy & les Lemmings");
        //InputFile inputFile = new InputFile(new File("X:\\test.mp4"), "Grizzy découvre la console de jeux vidéo - Grizzy & les Lemmings.mp4");
        message.setVideo(inputFile);
        message.setWidth(1920);
        message.setHeight(1080);
        //message.setThumbnail(inputFile);
        message.setHasSpoiler(true);

        System.out.println("process sending");
        try {
            execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    public void sendDocument(String chatId, String text, String url) throws Exception {
        SendDocument message = new SendDocument();
        message.setChatId(chatId);
        message.setCaption(text);
        message.setParseMode("HTML");
        HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
        message.setDocument(new InputFile(connection.getInputStream(), "Grizzy découvre la console de jeux vidéo - Grizzy & les Lemmings"));

        System.out.println("process sending");
        try {
            execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    public void sendTextMessage(String chatId, String text) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText(text);
        message.enableHtml(true);
        try {
            execute(message);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public String getBotUsername() {
        return "ADNotifyBot";
    }

    @Override
    public String getBotToken() {
        return token;
    }*/
}
