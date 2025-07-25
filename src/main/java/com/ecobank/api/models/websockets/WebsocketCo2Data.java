package com.ecobank.api.models.websockets;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class WebsocketCo2Data {
    private long id;
    private double co2BuyStock;
    private double co2SellStock;
}
