package com.ecobank.api.controllers;

import com.ecobank.api.models.transfer.TransferRequest;
import com.ecobank.api.services.AuthenticationService;
import com.ecobank.api.services.TransferService;
import com.ecobank.api.services.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/transfer")
public class TransferController {

    private final AuthenticationService authenticationService;
    private final TransferService transferService;

    public TransferController(AuthenticationService authenticationService, TransferService transferService) {
        this.authenticationService = authenticationService;
        this.transferService = transferService;
    }

    @GetMapping("/")
    public ResponseEntity<Object> Test(){
        return ResponseEntity.ok("Payment successful");
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
}
