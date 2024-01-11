package com.ecobank.api.services.abstractions;

import com.ecobank.api.database.entities.Account;
import com.ecobank.api.database.entities.User;

import javax.swing.text.html.Option;
import java.math.BigDecimal;
import java.util.Optional;

public interface IAccountService {
    Optional<Account> getAccountsByUserEmail(String email);
    Optional<Account> getAccountsByIBAN(String iban);

    Optional<Account> createAccountForUser(String email, String currency);

    Account getBankAccount();

    boolean tryChangeAmount(Account account, BigDecimal amount);

}
