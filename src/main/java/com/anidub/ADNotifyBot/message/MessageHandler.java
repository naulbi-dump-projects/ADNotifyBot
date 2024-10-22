/*package com.anidub.ADNotifyBot.message;

import lombok.*;
import org.telegram.telegrambots.meta.generics.*;
import org.telegram.telegrambots.meta.api.objects.*;
import org.telegram.telegrambots.longpolling.util.*;
import org.telegram.telegrambots.meta.api.methods.send.*;

@AllArgsConstructor(access = AccessLevel.PUBLIC)
public class MessageHandler implements LongPollingSingleThreadUpdateConsumer {
    private final TelegramClient telegramClient;

    @Override
    public void consume(Update update) {
        if(update.hasMessage() && update.getMessage().isUserMessage()) {
            sendTextMessage(update.getMessage().getChatId() + "", "Текущая версия бота непозволяет использовать его в личных сообщениях.\n\nНаш основной канал: @anidubnotify");
        }
    }

    @SneakyThrows
    public void sendImage(String chatId, String text, String url) {
        telegramClient.execute(SendPhoto.builder()
                .chatId(chatId)
                .caption(text)
                .parseMode("HTML")
                .photo(new InputFile(url))
                .build());
    }

    @SneakyThrows
    public void sendTextMessage(String chatId, String text) {
        telegramClient.execute(SendMessage.builder()
                .chatId(chatId)
                .text(text)
                .parseMode("HTML")
                .build());
    }
}
*/