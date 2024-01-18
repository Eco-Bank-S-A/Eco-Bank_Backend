package com.ecobank.api.controllers;

import com.ecobank.api.models.websockets.ClientRequest;
import com.ecobank.api.models.websockets.ClientCommandResponse;
import com.ecobank.api.services.ChatBroker;
import com.ecobank.api.services.Co2StockRateService;
import com.ecobank.api.services.JwtService;
import com.ecobank.api.services.subscribers.ChatSubscriber;
import com.ecobank.api.services.subscribers.Co2Subscriber;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.HashMap;


@Component
public class WebSocketController extends TextWebSocketHandler {

    Co2StockRateService co2Service;

    private HashMap<String, WebSocketSession> sessions = new HashMap<>();
    private  ChatBroker chatBroker;
    private JwtService jwtService;

    public WebSocketController(Co2StockRateService co2Service, ChatBroker chatBroker, JwtService jwtService) {
        this.co2Service = co2Service;
        this.chatBroker = chatBroker;
        this.jwtService = jwtService;
    }


    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message)
            throws Exception {


        var mapper = new ObjectMapper();
        var token = session.getUri().getQuery().split("token=")[1];
        var tokenData = jwtService.verifyToken(token);


        ClientRequest clientRequest;
        try {
            clientRequest = mapper.readValue(message.getPayload(), ClientRequest.class);

        } catch (Exception e) {
            var response = mapper.writeValueAsString(new ClientCommandResponse("WRONG_REQUEST"));
            session.sendMessage(new TextMessage(response));
            return;
        }

        if (clientRequest.getCommand().equals("SUBSCRIBE_CO2")) {
            co2Service.subscribe(new Co2Subscriber(session));
            var response = mapper.writeValueAsString(new ClientCommandResponse("SUBSCRIBED_CO2"));
            session.sendMessage(new TextMessage(response));
        }

        if (clientRequest.getCommand().equals("UNSUBSCRIBE_CO2")) {
            co2Service.unsubscribe(session.getId());
            var response = mapper.writeValueAsString(new ClientCommandResponse("UNSUBSCRIBED_CO2"));
            session.sendMessage(new TextMessage(response));
        }

        if (clientRequest.getCommand().equals("SUBSCRIBE_CHAT")) {
            try {
                var chatId = Long.parseLong(clientRequest.getPayload());

                chatBroker.subscribe(chatId, new ChatSubscriber(session, tokenData.getUserEmail()));
                var response = mapper.writeValueAsString(new ClientCommandResponse("SUBSCRIBED_CHAT"));
                session.sendMessage(new TextMessage(response));
            } catch (NumberFormatException e) {}
        }

        if (clientRequest.getCommand().equals("UNSUBSCRIBE_CHAT")) {
            try {
                var chatId = Long.parseLong(clientRequest.getPayload());

                chatBroker.unsubscribe(chatId, session.getId());
                var response = mapper.writeValueAsString(new ClientCommandResponse("UNSUBSCRIBED_CHAT"));
                session.sendMessage(new TextMessage(response));
            } catch (NumberFormatException e) {}
        }
    }
    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        sessions.put(session.getId(), session);
        System.out.println("New connection: " + session);
    }
    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        sessions.put(session.getId(), session);
        co2Service.unsubscribe(session.getId());
        chatBroker.unsubscribeAll(session.getId());
        System.out.println("Connection closed. Status: " + status);
    }
}
