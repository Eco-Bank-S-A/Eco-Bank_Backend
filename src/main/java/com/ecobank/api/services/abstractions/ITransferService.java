package com.ecobank.api.services.abstractions;

import com.ecobank.api.database.entities.User;

import java.math.BigDecimal;

public interface ITransferService {
    boolean transferMoney(String userEmail, String recipientIBAN, String title, BigDecimal amount);
}
