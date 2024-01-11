package com.ecobank.api.configuration;

import com.ecobank.api.controllers.WebSocketController;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.*;

@Configuration
@EnableWebSocket
public class WebSocketConfiguration implements WebSocketConfigurer {

    private final WebSocketController controller;
    public WebSocketConfiguration(WebSocketController controller) {
        this.controller = controller;
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(controller, "/websocket")
                .setAllowedOrigins("*");
    }
}