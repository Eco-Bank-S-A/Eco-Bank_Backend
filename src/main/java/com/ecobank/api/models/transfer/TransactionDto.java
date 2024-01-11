package com.ecobank.api.models.transfer;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;


@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class TransactionDto {
    private String additionalInfo;
    private BigDecimal balance;
    private Long CO2;

    private TransactionUserDto contact;
    private boolean isReceiver;

    private LocalDateTime createdAt;
}
