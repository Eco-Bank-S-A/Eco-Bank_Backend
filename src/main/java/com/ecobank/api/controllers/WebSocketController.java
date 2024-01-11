package com.ecobank.api.controllers;

import com.ecobank.api.models.websockets.ClientCo2Response;
import com.ecobank.api.models.websockets.ClientRequest;
import com.ecobank.api.models.websockets.ClientCommandResponse;
import com.ecobank.api.services.Co2StockRateService;
import com.ecobank.api.services.abstractions.co2.ICo2Subscriber;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;


@Component
public class WebSocketController extends TextWebSocketHandler implements ICo2Subscriber {

    Co2StockRateService co2Service;
    private WebSocketSession session;

    public WebSocketController(Co2StockRateService co2Service) {
        this.co2Service = co2Service;
    }


    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message)
            throws Exception {

        var mapper = new ObjectMapper();

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

        }

        if (clientRequest.getCommand().equals("UNSUBSCRIBE_CO2")) {
            co2Service.unsubscribe(this);
            var response = mapper.writeValueAsString(new ClientCommandResponse("UNSUBSCRIBED_CO2"));

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
        System.out.println("Connection closed. Status: " + status);
    }

    @Override
    public void notify(double co2StockRate) {
        var mapper = new ObjectMapper();
        var response = new ClientCo2Response("CO2_RATE", co2StockRate);
        try {
            var message = mapper.writeValueAsString(response);
            session.sendMessage(new TextMessage(message));
        } catch (Exception e) {
            System.out.println("Error sending message to client");
        }
    }
}
