package com.ecobank.api.controllers;

import com.ecobank.api.Constants;
import com.ecobank.api.database.entities.Co2Stock;
import com.ecobank.api.database.enums.TransactionStatus;
import com.ecobank.api.database.repositories.IAccountRepository;
import com.ecobank.api.database.repositories.ICo2StockRepository;
import com.ecobank.api.models.co2.BuySellCo2StockRequest;
import com.ecobank.api.services.AccountService;
import com.ecobank.api.services.abstractions.IAccountService;
import com.ecobank.api.services.abstractions.ITransferService;
import com.ecobank.api.services.abstractions.IUserService;
import org.springframework.http.HttpStatus;
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
    private final IAccountService accountService;

    public Co2Controller(ICo2StockRepository co2StockRepository, IUserService userService, ITransferService transferService,
                        IAccountService accountService) {
        this.co2StockRepository = co2StockRepository;
        this.userService = userService;
        this.transferService = transferService;
        this.accountService = accountService;
    }

    @GetMapping("/history")
    public ResponseEntity<ArrayList<Co2Stock>> getHistory() {
        var co2Stocks = co2StockRepository.findByCreatedAtAfterOrderByCreatedAt(LocalDateTime.now(ZoneOffset.UTC).minusDays(14));

        co2Stocks = new ArrayList<>(co2Stocks.subList(co2Stocks.size() - 400, co2Stocks.size()));

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

        var account = accountService.getAccountsByUserEmail(userEmail);
        var bankAccount = accountService.getAccountsByIBAN(Constants.BankAccountIBAN);

        if(account.isEmpty() || bankAccount.isEmpty()){
            return new ResponseEntity<>("Nie znaleziono konta", HttpStatus.NOT_FOUND);
        }
        var isTransferSuccessful = transferService.tryTransferMoney(account.get(), bankAccount.get(), price);
        transferService.createTransaction(account.get(), bankAccount.get(), isTransferSuccessful ? TransactionStatus.APPROVED : TransactionStatus.REJECTED, price, 0L,  Optional.of("CO2 Stock"));

        if (!isTransferSuccessful) {
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

        try {
            userService.changeMaxCo2(user.get(), user.get().getMaxCO2() - request.getAmount());

        } catch (Exception e) {
            System.out.println("User has not enough CO2");
            return ResponseEntity.badRequest().build();
        }

        return ResponseEntity.ok().build();
    }


    private Optional<Co2Stock> getCurrentStock(long id) {
        var co2StockOptional = co2StockRepository.findById(id);
        if (co2StockOptional.isEmpty()) {
            System.out.println("Stock not found");
            return Optional.empty();
        }

        var co2Stock = co2StockOptional.get();

        var maxDiff = LocalDateTime.now(ZoneOffset.UTC).minusSeconds(60*10);
        if (co2Stock.getCreatedAt().isBefore(maxDiff)) {
            System.out.println("Stock is too old");
            return Optional.empty();
        }

        return Optional.of(co2Stock);
    }

}
