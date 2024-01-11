package com.ecobank.api.services;

import com.ecobank.api.models.chat.ChatMessageDto;
import com.ecobank.api.services.abstractions.IChatSubscriber;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Dictionary;
import java.util.HashMap;

@Service
public class ChatBroker {
    private HashMap<Long, ArrayList<IChatSubscriber>> subscribers = new HashMap<>();

    public ChatBroker() {}

    public void publish(ChatMessageDto dto) {
        if (!subscribers.containsKey(dto.getChatId())) {
            return;
        }

        for (IChatSubscriber subscriber : subscribers.get(dto.getChatId())) {
            subscriber.notify(dto);
        }
    }


    public void subscribe(long chatId, IChatSubscriber subscriber) {
        if (!subscribers.containsKey(chatId)) {
            subscribers.put(chatId, new ArrayList<IChatSubscriber>());
        }

        subscribers.get(chatId).add(subscriber);
    }

    public void unsubscribe(long chatId, IChatSubscriber subscriber) {
        if (!subscribers.containsKey(chatId)) {
            return;
        }

        subscribers.get(chatId).remove(subscriber);
    }

    public void unsubscribeAll(IChatSubscriber subscriber) {
        for (var entry : subscribers.entrySet()) {
            entry.getValue().remove(subscriber);
        }
    }
}
