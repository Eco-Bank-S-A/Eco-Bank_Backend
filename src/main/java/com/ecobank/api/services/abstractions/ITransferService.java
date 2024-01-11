package com.ecobank.api.services.abstractions;

import com.ecobank.api.database.entities.Account;
import com.ecobank.api.database.entities.Transaction;
import com.ecobank.api.database.entities.User;

import java.math.BigDecimal;
import java.util.ArrayList;

public interface ITransferService {
    boolean transferMoney(String userEmail, String recipientIBAN, String title, BigDecimal amount);

    boolean transferToBank(String userEmail, String title, BigDecimal amount);
    boolean transferFromBank(String userEmail, String title, BigDecimal amount);

    ArrayList<Transaction> getFinalizedTransactionsByAccount(Account account);
}
