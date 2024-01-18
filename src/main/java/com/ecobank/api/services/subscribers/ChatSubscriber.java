package com.ecobank.api.services.subscribers;

import com.ecobank.api.models.chat.ChatMessageDto;
import com.ecobank.api.models.websockets.WebsocketCo2Data;
import com.ecobank.api.models.websockets.WebsocketResponseData;
import com.ecobank.api.services.abstractions.IChatSubscriber;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

public class ChatSubscriber implements IChatSubscriber {
    private final WebSocketSession session;
    private final String userEmail;

    public ChatSubscriber(WebSocketSession session, String userEmail) {
        this.session = session;
        this.userEmail = userEmail;
    }


    @Override
    public String getSessionId() {
        return session.getId();
    }

    @Override
    public String getUserEmail() {
        return userEmail;
    }

    @Override
    public void notifyChat(ChatMessageDto message) {
        var mapper = new ObjectMapper();
        var response = WebsocketResponseData.builder()
                .command("CHAT_NEW_MESSAGE:"+message.getChatId())
                .data(message)
                .build();
        try {
            var json = mapper.writeValueAsString(response);
            session.sendMessage(new TextMessage(json));
        } catch (Exception e) {
            System.out.println("Error sending message to client");
        }
    }
}
