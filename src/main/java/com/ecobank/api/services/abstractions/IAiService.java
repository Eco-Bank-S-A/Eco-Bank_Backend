package com.ecobank.api.services.abstractions;

import com.ecobank.api.database.entities.Account;

import java.math.BigDecimal;

public interface IAiService {
    boolean predictFraud(BigDecimal balance, double creditScore, BigDecimal amount);
    int getCreditScore(Account account);
}
