package com.ecobank.api.controllers;

import com.ecobank.api.database.repositories.ICo2StockRepository;
import com.ecobank.api.models.co2.BuySellCo2StockRequest;
import com.ecobank.api.models.company.CallbackUrlRequest;
import com.ecobank.api.services.abstractions.IAccountService;
import com.ecobank.api.services.abstractions.ICompanyService;
import com.ecobank.api.services.abstractions.ITransferService;
import com.ecobank.api.services.abstractions.IUserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/company")
public class CompanyController {
    private final ICo2StockRepository co2StockRepository;
    private final IUserService userService;
    private final ITransferService transferService;
    private final ICompanyService companyService;
    private final IAccountService accountService;

    public CompanyController(ICo2StockRepository co2StockRepository, IUserService userService, ITransferService transferService,
                            ICompanyService companyService, IAccountService accountService) {
        this.co2StockRepository = co2StockRepository;
        this.userService = userService;
        this.transferService = transferService;
        this.companyService = companyService;
        this.accountService = accountService;

    }
    @PutMapping("/setCallbackUrl")
    public ResponseEntity buyCo2Stock(@RequestBody CallbackUrlRequest url) {
        var email = SecurityContextHolder.getContext().getAuthentication().getName();
        var account = accountService.getAccountsByUserEmail(email);
        if(account.isEmpty())
            return new ResponseEntity<>("Nie znaleziono konta", HttpStatus.NOT_FOUND);
        var company = account.get().getCompany();
        if(company == null)
            return new ResponseEntity<>("Nie znaleziono firmy", HttpStatus.NOT_FOUND);

        companyService.setCompanyCallbackUrl(company, url.getUrl());
        return ResponseEntity.ok().build();
    }
}
