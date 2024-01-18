package com.ecobank.api.services.subscribers;

import com.ecobank.api.models.websockets.ClientRequest;
import com.ecobank.api.models.websockets.WebsocketCo2Data;
import com.ecobank.api.models.websockets.WebsocketResponseData;
import com.ecobank.api.services.abstractions.co2.ICo2Subscriber;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

public class Co2Subscriber implements ICo2Subscriber {
    private final WebSocketSession session;

    public Co2Subscriber(WebSocketSession session) {
        this.session = session;
    }

    @Override
    public String getSessionId() {
        return session.getId();
    }

    @Override
    public void notifyCo2(long id, double co2BuyStock, double co2SellStock) {
        var mapper = new ObjectMapper();
        var response = WebsocketResponseData.builder()
                .command("CO2_RATE")
                    .data(
                        WebsocketCo2Data
                            .builder()
                            .id(id)
                            .co2BuyStock(co2BuyStock)
                            .co2SellStock(co2SellStock)
                            .build()
                        ).build();
        try {
            var message = mapper.writeValueAsString(response);
            session.sendMessage(new TextMessage(message));
        } catch (Exception e) {
            System.out.println("Error sending message to client");
        }
    }
}
