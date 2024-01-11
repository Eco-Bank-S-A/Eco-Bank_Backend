package com.ecobank.api.services;

import com.ecobank.api.database.entities.Account;
import com.ecobank.api.database.entities.Transaction;
import com.ecobank.api.database.repositories.ITransactionRepository;
import com.ecobank.api.models.transfer.TransferInfo;
import com.ecobank.api.models.user.UserInfo;
import com.ecobank.api.services.abstractions.ITransferService;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;

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

    public int countTransferHistory(String userEmail, int[] operationsTypes, LocalDateTime from, LocalDateTime to){
        return transactionRepository.countTransactionHistory(userEmail, operationsTypes, from, to);
    }
    @Override
    public List<TransferInfo> getTransferHistory(String userEmail, int[] operationsTypes, LocalDateTime from, LocalDateTime to, int batchSize, int batchNumber) {
        Pageable batch = PageRequest.of(batchNumber, batchSize);
        List<Transaction> transactions = transactionRepository.findTransactionHistory(userEmail, operationsTypes, from, to, batch);
        return convertToTransferInfoList(transactions);
    }

    private List<TransferInfo> convertToTransferInfoList(List<Transaction> transactions) {
        return transactions.stream()
                .map(transaction -> new TransferInfo(transaction))
                .collect(Collectors.toList());
    }
    private Transaction createTransaction(Account sender, Account receiver, int status, BigDecimal balance, Long CO2, Optional<String> additionalInfo) {
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
