package com.ecobank.api.services.abstractions;

import com.ecobank.api.models.chat.ChatMessageDto;

public interface IChatSubscriber {

    String getSessionId();
    String getUserEmail();
    void notifyChat(ChatMessageDto message);
}
