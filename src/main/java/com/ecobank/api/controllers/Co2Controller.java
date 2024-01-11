package com.ecobank.api.controllers;

import com.ecobank.api.Constants;
import com.ecobank.api.database.entities.Co2Stock;
import com.ecobank.api.database.repositories.ICo2StockRepository;
import com.ecobank.api.models.co2.BuySellCo2StockRequest;
import com.ecobank.api.services.abstractions.ITransferService;
import com.ecobank.api.services.abstractions.IUserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Optional;

@RestController
@RequestMapping("/api/co2")
public class Co2Controller {

    private final ICo2StockRepository co2StockRepository;
    private final IUserService userService;
    private final ITransferService transferService;

    public Co2Controller(ICo2StockRepository co2StockRepository, IUserService userService, ITransferService transferService) {
        this.co2StockRepository = co2StockRepository;
        this.userService = userService;
        this.transferService = transferService;
    }

    @GetMapping("/history")
    public ResponseEntity<ArrayList<Co2Stock>> getHistory() {
        var co2Stocks = co2StockRepository.findByCreatedAtAfter(LocalDateTime.now(ZoneOffset.UTC).minusDays(14));

        return ResponseEntity.ok(co2Stocks);
    }

    @PostMapping("/buy")
    public ResponseEntity buyCo2Stock(@RequestBody BuySellCo2StockRequest request) {
        var co2StockOptional = getCurrentStock(request.getStockId());
        if (co2StockOptional.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        var co2Stock = co2StockOptional.get();

        var userEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        var price = BigDecimal.valueOf(co2Stock.getCo2BuyStock()).multiply(BigDecimal.valueOf(request.getAmount()));
        var isSuccessful = transferService.transferToBank(userEmail, "CO2 Stock", price);

        if (!isSuccessful) {
            return ResponseEntity.badRequest().build();
        }

        var user = userService.getUserByEmail(userEmail);
        userService.changeMaxCo2(user.get(), user.get().getMaxCO2() + request.getAmount());

        return ResponseEntity.ok().build();
    }

    @PostMapping("/sell")
    public ResponseEntity sellCo2Stock(@RequestBody BuySellCo2StockRequest request) {
        var co2StockOptional = getCurrentStock(request.getStockId());
        if (co2StockOptional.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        var co2Stock = co2StockOptional.get();

        var userEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        var price = BigDecimal.valueOf(co2Stock.getCo2BuyStock()).multiply(BigDecimal.valueOf(request.getAmount()));
        transferService.transferFromBank(userEmail, "CO2 Stock", price);

        var user = userService.getUserByEmail(userEmail);
        userService.changeMaxCo2(user.get(), user.get().getMaxCO2() - request.getAmount());

        return ResponseEntity.ok().build();
    }


    private Optional<Co2Stock> getCurrentStock(long id) {
        var co2StockOptional = co2StockRepository.findById(id);
        if (co2StockOptional.isEmpty()) {
            return Optional.empty();
        }

        var co2Stock = co2StockOptional.get();

        var maxDiff = LocalDateTime.now(ZoneOffset.UTC).minusSeconds(30);
        if (co2Stock.getCreatedAt().isBefore(maxDiff)) {
            System.out.println("Stock is too old");
            return Optional.empty();
        }

        return Optional.of(co2Stock);
    }

}
