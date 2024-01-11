package com.ecobank.api.controllers;

import com.ecobank.api.database.entities.Account;
import com.ecobank.api.database.entities.Transaction;
import com.ecobank.api.models.transfer.TransactionDto;
import com.ecobank.api.models.transfer.TransactionUserDto;
import com.ecobank.api.models.transfer.TransferRequest;
import com.ecobank.api.services.AccountService;
import com.ecobank.api.services.AuthenticationService;
import com.ecobank.api.services.TransferService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.lang.reflect.Array;
import java.util.ArrayList;

@RestController
@RequestMapping("/api/transfer")
public class TransferController {

    private final AuthenticationService authenticationService;
    private final TransferService transferService;

    private final AccountService accountService;

    public TransferController(AuthenticationService authenticationService, TransferService transferService, AccountService accountService) {
        this.authenticationService = authenticationService;
        this.transferService = transferService;
        this.accountService = accountService;
    }

    @GetMapping()
    public ResponseEntity<ArrayList<TransactionDto>> Test(){
        var userEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        var account = accountService.getAccountsByUserEmail(userEmail);
        if (account.isEmpty())
            return ResponseEntity.badRequest().build();

        var transactions = transferService.getFinalizedTransactionsByAccount(account.get());
        var transactionDtos = new ArrayList<TransactionDto>();

        for (Transaction transaction : transactions) {
            Account extAccount;
            boolean isReceiver = false;
            if (!transaction.getSender().getId().equals(account.get().getId())) {
                extAccount = transaction.getSender();
                isReceiver = true;
            }
            else
                extAccount = transaction.getReceiver();

            var dto = TransactionDto
                    .builder()
                    .additionalInfo(transaction.getAdditionalInfo())
                    .balance(transaction.getBalance())
                    .CO2(transaction.getCO2())
                    .createdAt(transaction.getCreatedAt())
                    .contact(
                            TransactionUserDto
                                    .builder()
                                    .iban(extAccount.getIBAN())
                                    .name(extAccount.getUser().getFirstName() + " " + extAccount.getUser().getLastName())
                                    .build()
                    ).isReceiver(isReceiver)
                    .build();
            transactionDtos.add(dto);
        }

        return ResponseEntity.ok(transactionDtos);
    }
    @PostMapping("/pay")
    public ResponseEntity<Object> pay(@RequestBody TransferRequest transferRequest) {
        System.out.println(transferRequest.getRecipientIBAN());
        String userEmail = SecurityContextHolder.getContext().getAuthentication().getName();

        boolean success;
        try {
            success = transferService.transferMoney(userEmail, transferRequest.getRecipientIBAN(), transferRequest.getTitle(), transferRequest.getAmount());
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return ResponseEntity.internalServerError().build();
        }


        if (success) {
            return ResponseEntity.ok("Payment successful");
        } else {
            return ResponseEntity.badRequest().body("Insufficient funds or recipient not found");
        }
    }
}
