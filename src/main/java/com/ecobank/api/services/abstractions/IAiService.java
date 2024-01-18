package com.ecobank.api.services.abstractions;

import java.math.BigDecimal;

public interface IAiService {
    boolean predictFraud(BigDecimal balance, double creditScore, BigDecimal amount);
}
