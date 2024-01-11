package com.ecobank.api.services.abstractions;

import com.ecobank.api.models.chat.ChatMessageDto;

public interface IChatSubscriber {
    void notify(ChatMessageDto message);
}
