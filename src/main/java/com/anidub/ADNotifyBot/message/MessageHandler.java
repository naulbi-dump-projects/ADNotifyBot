package com.anidub.ADNotifyBot.message;

import lombok.*;
import org.telegram.telegrambots.bots.*;
import org.telegram.telegrambots.meta.api.objects.*;
import org.telegram.telegrambots.meta.api.methods.send.*;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@AllArgsConstructor(access = AccessLevel.PUBLIC)
public class MessageHandler extends TelegramLongPollingBot {
    public String token;

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
    }
}
