package com.ecobank.api.models.co2;

import lombok.Data;

@Data
public class BuySellCo2StockRequest {
    private long StockId;
    private long Amount;
}
