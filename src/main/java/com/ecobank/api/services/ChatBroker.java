package com.ecobank.api.services;

import com.ecobank.api.controllers.WebSocketController;
import com.ecobank.api.models.chat.ChatMessageDto;
import com.ecobank.api.services.abstractions.IChatSubscriber;
import com.ecobank.api.services.subscribers.ChatSubscriber;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Dictionary;
import java.util.HashMap;

@Service
public class ChatBroker {
    private HashMap<Long, ArrayList<ChatSubscriber>> subscribers = new HashMap<>();

    public void publish(String senderEmail, ChatMessageDto dto) {
        if (!subscribers.containsKey(dto.getChatId())) {
            return;
        }

        System.out.println("Publishing message to chat " + dto.getChatId());
        for (ChatSubscriber subscriber : subscribers.get(dto.getChatId())) {
            if (subscriber.getUserEmail().equals(senderEmail)) {
                continue;
            }

            subscriber.notifyChat(dto);
        }
    }


    public void subscribe(long chatId, ChatSubscriber subscriber) {
        if (!subscribers.containsKey(chatId)) {
            subscribers.put(chatId, new ArrayList<>());
        }

        subscribers.get(chatId).add(subscriber);
    }

    public void unsubscribe(long chatId, String sessionId) {
        if (!subscribers.containsKey(chatId)) {
            return;
        }


        var arr = subscribers.get(chatId);
        for(var sub: arr) {
            if (sub.getSessionId().equals(sessionId)) {
                arr.remove(sub);
                return;
            }
        }
    }

    public void unsubscribeAll(String sessionId) {
        for (var entry : subscribers.entrySet()) {
            for (var subscriber : entry.getValue()) {
                if (subscriber.getSessionId().equals(sessionId)) {
                    entry.getValue().remove(subscriber);
                    break;
                }
            }
        }
    }
}
