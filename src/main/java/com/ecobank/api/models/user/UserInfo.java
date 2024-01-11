package com.ecobank.api.models.user;

import com.ecobank.api.database.entities.Account;
import com.ecobank.api.database.entities.User;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class UserInfo {
    public UserInfo(User user, Account account){
        email = user.getEmail();
        IBAN = account.getIBAN();
        balance = account.getBalance();
        freeFunds = account.getFreeFunds();
        lastName = user.getLastName();
        firstName = user.getFirstName();
        pesel = user.getPesel();
        phone = user.getPhone();
        CO2 = user.getCO2();
        maxCO2 = user.getMaxCO2();
        AccountType = account.getAccountType().getType();
    }

    private String email;
    private String IBAN;
    private BigDecimal balance;
    private BigDecimal freeFunds;
    private String lastName;
    private String firstName;
    private String pesel;
    private String phone;
    private Long CO2;
    private Long maxCO2;
    private String AccountType;
}
