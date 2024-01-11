package com.ecobank.api.controllers;

import com.ecobank.api.models.general.PaginationResponseModel;
import com.ecobank.api.models.transfer.TransferRequest;
import com.ecobank.api.services.AuthenticationService;
import com.ecobank.api.services.TransferService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/transfer")
public class TransferController {

    private final AuthenticationService authenticationService;
    private final TransferService transferService;

    public TransferController(AuthenticationService authenticationService, TransferService transferService) {
        this.authenticationService = authenticationService;
        this.transferService = transferService;
    }
    @PostMapping("/pay")
    public ResponseEntity<Object> pay(@RequestBody TransferRequest transferRequest) {
        String userEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        boolean success = transferService.transferMoney(userEmail, transferRequest.getRecipientIBAN(), transferRequest.getTitle(), transferRequest.getAmount());

        if (success) {
            return ResponseEntity.ok("Payment successful");
        } else {
            return ResponseEntity.badRequest().body("Insufficient funds or recipient not found");
        }
    }
    @GetMapping("/history")
    public ResponseEntity<Object> getTransferHistory(
            @RequestParam int pageSize,
            @RequestParam int currentPage,
            @RequestParam int[] operationTypes,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss") LocalDateTime from,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss") LocalDateTime to
    )
    {
        String userEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        var content = transferService.getTransferHistory(userEmail, operationTypes, from, to, pageSize, currentPage);
        var totalCount = transferService.countTransferHistory(userEmail, operationTypes, from, to);
        var response = new PaginationResponseModel(content, pageSize, currentPage, totalCount);
        return ResponseEntity.ok(response);
    }
}
