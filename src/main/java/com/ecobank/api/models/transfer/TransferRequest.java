package com.ecobank.api.models.transfer;

import lombok.Data;

import java.math.BigDecimal;
@Data
public class TransferRequest {

    private String recipientIBAN;
    private String title;
    private BigDecimal amount;

    // Constructors, getters, and setters
}
