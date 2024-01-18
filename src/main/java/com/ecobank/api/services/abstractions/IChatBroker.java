package com.ecobank.api.services.abstractions;

import com.ecobank.api.models.chat.ChatMessageDto;
import com.ecobank.api.services.subscribers.ChatSubscriber;

public interface IChatBroker {
    void publish(String senderEmail, ChatMessageDto dto);

    void subscribe(long chatId, ChatSubscriber subscriber);

    void unsubscribe(long chatId, String sessionId);

    void unsubscribeAll(String sessionId);
}
