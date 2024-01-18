package com.ecobank.api.services.abstractions;

import com.ecobank.api.database.entities.Account;
import com.ecobank.api.database.entities.Transaction;
import com.ecobank.api.database.enums.TransactionStatus;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Optional;

public interface ITransferService {
    boolean transferFromBank(String userEmail, String title, BigDecimal amount);
    boolean tryTransferMoney(Account account, Account recipientAccount, BigDecimal amount);
    Optional<Transaction> getTransactionByUuid(String uuid);
    Transaction editTransaction(Transaction transaction);
    Transaction createTransaction(Account sender, Account receiver, TransactionStatus status, BigDecimal balance, Long CO2);
    Transaction createTransaction(Account sender, Account receiver, TransactionStatus status, BigDecimal balance, Long CO2, Optional<String> additionalInfo);
    ArrayList<Transaction> getFinalizedTransactionsByAccount(Account account);
}
