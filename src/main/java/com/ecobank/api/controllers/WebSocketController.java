package com.ecobank.api.controllers;

import com.ecobank.api.models.chat.ChatMessageDto;
import com.ecobank.api.models.websockets.ClientCo2Response;
import com.ecobank.api.models.websockets.ClientRequest;
import com.ecobank.api.models.websockets.ClientCommandResponse;
import com.ecobank.api.services.ChatBroker;
import com.ecobank.api.services.Co2StockRateService;
import com.ecobank.api.services.abstractions.IChatSubscriber;
import com.ecobank.api.services.abstractions.co2.ICo2Subscriber;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;


@Component
public class WebSocketController extends TextWebSocketHandler implements ICo2Subscriber, IChatSubscriber {

    Co2StockRateService co2Service;
    private WebSocketSession session;
    private  ChatBroker chatBroker;

    public WebSocketController(Co2StockRateService co2Service, ChatBroker chatBroker) {
        this.co2Service = co2Service;
        this.chatBroker = chatBroker;
    }


    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message)
            throws Exception {

        var mapper = new ObjectMapper();
        System.out.println("TOKEN: " + session.getHandshakeHeaders().get("Authorization"));

        ClientRequest clientRequest;
        try {
            clientRequest = mapper.readValue(message.getPayload(), ClientRequest.class);

        } catch (Exception e) {
            var response = mapper.writeValueAsString(new ClientCommandResponse("WRONG_REQUEST"));
            session.sendMessage(new TextMessage(response));
            session.close();
            return;
        }

        if (clientRequest.getCommand().equals("SUBSCRIBE_CO2")) {
            co2Service.subscribe(this);
            var response = mapper.writeValueAsString(new ClientCommandResponse("SUBSCRIBED_CO2"));
            session.sendMessage(new TextMessage(response));
        }

        if (clientRequest.getCommand().equals("UNSUBSCRIBE_CO2")) {
            co2Service.unsubscribe(this);
            var response = mapper.writeValueAsString(new ClientCommandResponse("UNSUBSCRIBED_CO2"));
            session.sendMessage(new TextMessage(response));
        }

//        if (clientMessage.startsWith("hello") || clientMessage.startsWith("greet")) {
//            session.sendMessage(new TextMessage("Hello there!"));
//        } else if (clientMessage.startsWith("time")) {
//            var currentTime = LocalTime.now();
//            session.sendMessage(new TextMessage(currentTime.toString()));
//        } else {
//
//            session.sendMessage(new TextMessage("Unknown command"));
//        }
    }
    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        this.session = session;
        System.out.println("New connection: " + session);
    }
    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        this.session = null;
        co2Service.unsubscribe(this);
        chatBroker.unsubscribeAll(this);
        System.out.println("Connection closed. Status: " + status);
    }

    @Override
    public void notify(long id, double co2BuyStock, double co2SellStock) {
        var mapper = new ObjectMapper();
        var response = ClientCo2Response
            .builder()
            .command("CO2_RATE")
            .id(id)
            .co2BuyStock(co2BuyStock)
            .co2SellStock(co2SellStock)
            .build();

        try {
            var message = mapper.writeValueAsString(response);
            session.sendMessage(new TextMessage(message));
        } catch (Exception e) {
            System.out.println("Error sending message to client");
        }
    }

    @Override
    public void notify(ChatMessageDto dto) {

    }
}
