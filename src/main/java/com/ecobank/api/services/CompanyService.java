package com.ecobank.api.services;

import com.ecobank.api.database.entities.Account;
import com.ecobank.api.database.entities.Company;
import com.ecobank.api.database.entities.User;
import com.ecobank.api.database.repositories.ICompanyRepository;
import com.ecobank.api.services.abstractions.ICompanyService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
@Service
public class CompanyService implements ICompanyService {

    private ICompanyRepository companyRepository;
    public CompanyService(ICompanyRepository companyRepository){
        this.companyRepository = companyRepository;
    }
    @Override
    public Long calculateCO2(Company company, BigDecimal amount) {
        return amount.multiply(BigDecimal.valueOf(company.getCO2Factor())).longValue();
    }

    @Override
    public Company getCompanyFromAccount(Account account) {
        return account.getCompany();
    }

    @Override
    public void setCompanyCallbackUrl(Company company, String url) {
        company.setCallbackUrl(url);
        companyRepository.save(company);
    }
}
