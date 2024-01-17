package com.ecobank.api.models.transfer;


import lombok.Data;

import java.math.BigDecimal;

@Data
public class TransferAttemptRequest {
    private String SenderIBAN;
    private BigDecimal Amount;
    private String AdditionalInfo;
}
