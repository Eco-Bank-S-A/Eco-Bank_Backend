package com.ecobank.api.services;

import com.ecobank.api.Constants;
import com.ecobank.api.database.entities.Account;
import com.ecobank.api.database.entities.Transaction;
import com.ecobank.api.database.enums.TransactionStatus;
import com.ecobank.api.database.repositories.ITransactionRepository;
import com.ecobank.api.services.abstractions.ITransferService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.NoSuchElementException;
import java.util.Optional;
@Service
public class TransferService implements ITransferService {
    private final AccountService accountService;
    private final ITransactionRepository transactionRepository;
    public TransferService(AccountService accountService, ITransactionRepository transactionRepository) {
        this.accountService = accountService;
        this.transactionRepository = transactionRepository;
    }
    @Override
    public boolean transferFromBank(String userEmail, String title, BigDecimal amount) {
        var optionalAccount = accountService.getAccountsByUserEmail(userEmail);
        Account account = optionalAccount.orElseThrow(() -> new NoSuchElementException("Account not found"));
        accountService.tryChangeAmount(account, amount);

        createTransaction(account, accountService.getBankAccount(),TransactionStatus.APPROVED, amount, 0L, Optional.ofNullable(title));
        return true;
    }

    @Override
    public Optional<Transaction> getTransactionByUuid(String uuid) {
        return transactionRepository.findTransactionByUuid(uuid);
    }

    @Override
    public ArrayList<Transaction> getFinalizedTransactionsByAccount(Account account) {
        return transactionRepository.findFinalizedTransactionsByAccount(account.getId());
    }
    @Override
    public Transaction createTransaction(Account sender, Account receiver, TransactionStatus status, BigDecimal balance, Long CO2) {
        return createTransaction(sender, receiver, status, balance, CO2, Optional.of(""));
    };
    @Override
    public Transaction editTransaction(Transaction transaction){
        return transactionRepository.save(transaction);
    }
    @Override
    public Transaction createTransaction(Account sender, Account receiver, TransactionStatus status, BigDecimal balance, Long CO2, Optional<String> additionalInfo) {
        Transaction transaction = new Transaction();
        transaction.setUuid(java.util.UUID.randomUUID().toString());
        transaction.setSender(sender);
        transaction.setReceiver(receiver);
        transaction.setStatus(status);
        transaction.setBalance(balance);
        transaction.setCO2(CO2);
        transaction.setCreatedAt(LocalDateTime.now(ZoneOffset.UTC));


        additionalInfo.ifPresent(transaction::setAdditionalInfo);

        return transactionRepository.save(transaction);
    }
    @Override
    public  boolean tryTransferMoney(Account account, Account recipientAccount, BigDecimal amount){
        if(!accountService.tryChangeAmount(account, amount.multiply(new BigDecimal(-1)))){
            return false;
        }
        if(accountService.tryChangeAmount(recipientAccount, amount)){
            return true;
        }
        if(!accountService.tryChangeAmount(account, amount)){
            //TODO NOTIFY BANK EMPLOYEE
        }
        return false;
    }
}
