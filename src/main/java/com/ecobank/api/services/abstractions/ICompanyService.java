package com.ecobank.api.services.abstractions;

import com.ecobank.api.database.entities.Account;
import com.ecobank.api.database.entities.Company;
import com.ecobank.api.database.entities.User;


import java.math.BigDecimal;
import java.util.Optional;

public interface ICompanyService {
    Long calculateCO2(Company company , BigDecimal amount);
    Company getCompanyFromAccount(Account account);
    void setCompanyCallbackUrl(Company company, String url);
}
