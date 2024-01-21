package com.ecobank.api.controllers;

import com.ecobank.api.database.entities.Account;
import com.ecobank.api.database.entities.Transaction;
import com.ecobank.api.database.enums.TransactionStatus;
import com.ecobank.api.models.co2.Co2StockPriceResponse;
import com.ecobank.api.models.transfer.TransactionDto;
import com.ecobank.api.models.transfer.TransactionUserDto;
import com.ecobank.api.models.transfer.TransferAttemptRequest;
import com.ecobank.api.models.transfer.TransferRequest;
import com.ecobank.api.services.abstractions.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.math.BigDecimal;

import com.ecobank.api.services.AiService;

@RestController
@RequestMapping("/api/transfer")
public class TransferController {

    private final IAuthenticationService authenticationService;
    private final ITransferService transferService;
    private final IUserService userService;
    private final IAccountService accountService;
    private final ICompanyService companyService;

    private final AiService aiService;

    public TransferController(IAuthenticationService authenticationService, ITransferService transferService, IAccountService accountService, ICompanyService companyService, IUserService userService, AiService aiService) {
        this.authenticationService = authenticationService;
        this.transferService = transferService;
        this.accountService = accountService;
        this.companyService = companyService;
        this.userService = userService;
        this.aiService = aiService;
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

    @PostMapping("/create")
    public ResponseEntity<String> create(@RequestBody TransferAttemptRequest transferAttemptRequest) {
        String userEmail = SecurityContextHolder.getContext().getAuthentication().getName();

        var user = userService.getUserByEmail(userEmail).get();
        var recieverAccount =  accountService.getAccountsByUserEmail(userEmail);



        var senderAccount = accountService.getAccountsByIBAN(transferAttemptRequest.getSenderIBAN());
        if(senderAccount.isEmpty() || recieverAccount.isEmpty())
            return new ResponseEntity<>("Nie znaleziono kont", HttpStatus.NOT_FOUND);
        var co2 = companyService.calculateCO2(recieverAccount.get().getCompany(), transferAttemptRequest.getAmount());
        var response = transferService.createTransaction(senderAccount.get(), recieverAccount.get(), TransactionStatus.PENDING, transferAttemptRequest.getAmount(), co2);
        return ResponseEntity.ok(response.getUuid());
    }
    @PutMapping("/confirm/{uuid}")
    public ResponseEntity<Object> confirm(@PathVariable(value="uuid") String uuid) {
        String userEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        var optionalSenderAccount = accountService.getAccountsByUserEmail(userEmail);
        var optionalTransaction = transferService.getTransactionByUuid(uuid);
        if(optionalTransaction.isEmpty() || optionalSenderAccount.isEmpty()){
            return new ResponseEntity<>("Nie znaleziono danych", HttpStatus.NOT_FOUND);
        }
        var senderAccount = optionalSenderAccount.get();
        var transaction = optionalTransaction.get();
        if(senderAccount != transaction.getSender() || transaction.getStatus() != TransactionStatus.PENDING){
            return new ResponseEntity<>("Bład!", HttpStatus.UNAUTHORIZED);
        }

        var receiverAccount = transaction.getReceiver();
        var company = companyService.getCompanyFromAccount(receiverAccount);
        if(company == null){
            return new ResponseEntity<>("Bład!", HttpStatus.UNAUTHORIZED);
        }

        var balance = transaction.getBalance();

        var isTransferSuccessful = transferService.tryTransferMoney(senderAccount, receiverAccount, transaction.getBalance());
        transaction.setStatus(isTransferSuccessful ? TransactionStatus.APPROVED : TransactionStatus.REJECTED);
        transferService.editTransaction(transaction);

        RestTemplate restTemplate = new RestTemplate();
        try {
            restTemplate.put(company.getCallbackUrl(), isTransferSuccessful ? "SUCCESS" : "FAILED");
        }
        catch (Exception e){
            return ResponseEntity.badRequest().body("Wytąpił bład podczas potwierdzania tranzakcji. Skontakuj się z obsługa klienta.");
        }

        if (isTransferSuccessful == false) {
            return ResponseEntity.badRequest().body("Wytąpił bład podczas realizacji tranzakcji. Skontakuj się z obsługa klienta.");
        }
        return ResponseEntity.ok("Płatnośc zakończona sukcesem");
    }
    @PostMapping("/pay")
    public ResponseEntity<Object> pay(@RequestBody TransferRequest transferRequest) {
        System.out.println(transferRequest.getRecipientIBAN());
        String userEmail = SecurityContextHolder.getContext().getAuthentication().getName();

        var account = accountService.getAccountsByUserEmail(userEmail);
        var recipientAccount = accountService.getAccountsByIBAN(transferRequest.getRecipientIBAN());

        BigDecimal balance = account.get().getBalance();
        BigDecimal amount = transferRequest.getAmount();
        final int creditScore = aiService.getCreditScore(recipientAccount.get());

        //System.out.println("________");
        //System.out.println(balance);
        //System.out.println(amount);

        if(account.isEmpty() || recipientAccount.isEmpty()){
            return new ResponseEntity<>("Nie znaleziono konta", HttpStatus.NOT_FOUND);
        }

        if(aiService.predictFraud(balance, creditScore, amount)) {
            System.out.println("Online fraud detected!");
            return ResponseEntity.status(451).body("Online fraud detected, payment cancelled!");
        }

        var isTransferSuccessful = transferService.tryTransferMoney(account.get(), recipientAccount.get(), transferRequest.getAmount());
        transferService.createTransaction(account.get(), recipientAccount.get(), isTransferSuccessful ? TransactionStatus.APPROVED : TransactionStatus.REJECTED, transferRequest.getAmount(), 0L, Optional.ofNullable(transferRequest.getTitle()));


        if (isTransferSuccessful == false) {
            return ResponseEntity.badRequest().body("Wytąpił bład podczas realizacji tranzakcji. Skontakuj się z obsługa klienta.");
        }
        return ResponseEntity.ok("Płatnośc zakończona sukcesem");

    }
}