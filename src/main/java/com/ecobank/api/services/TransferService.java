package com.ecobank.api.services;

import com.ecobank.api.database.entities.Account;
import com.ecobank.api.database.entities.Transaction;
import com.ecobank.api.database.entities.User;
import com.ecobank.api.database.repositories.ITransactionRepository;
import com.ecobank.api.services.abstractions.ITransferService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.NoSuchElementException;
import java.util.Optional;
@Service
public class TransferService implements ITransferService {
    private final AccountService accountService;
    private final ITransactionRepository transactionRepository;
    public TransferService(AccountService accountService, ITransactionRepository transactionRepository){
        this.accountService = accountService;
        this.transactionRepository = transactionRepository;
    }
    @Override
    public boolean transferMoney(String userEmail, String recipientIBAN, String title, BigDecimal amount) {

        var optionalAccount = accountService.getAccountsByUserEmail(userEmail);
        Account account = optionalAccount.orElseThrow(() -> new NoSuchElementException("Account not found"));
        var optionalRecipientAccount = accountService.getAccountsByIBAN(recipientIBAN);
        Account recipientAccount = optionalRecipientAccount.orElseThrow(() -> new NoSuchElementException("Account not found"));
        var isOperationSuccessful = tryFinalizeTransfers(account, recipientAccount, amount);
        createTransaction(account, recipientAccount, isOperationSuccessful ? 1 : 0, amount, 0L, Optional.ofNullable(title));
        return isOperationSuccessful;
    }
    public Transaction createTransaction(Account sender, Account receiver, int status, BigDecimal balance, Long CO2, Optional<String> additionalInfo) {
        Transaction transaction = new Transaction();
        transaction.setUuid(java.util.UUID.randomUUID().toString());
        transaction.setSender(sender);
        transaction.setReceiver(receiver);
        transaction.setStatus(status);
        transaction.setBalance(balance);
        transaction.setCO2(CO2);

        additionalInfo.ifPresent(transaction::setAdditionalInfo);

        return transactionRepository.save(transaction);
    }
    private boolean tryFinalizeTransfers(Account account, Account recipientAccount, BigDecimal amount){
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
