package com.ecobank.api.services.abstractions;

import com.ecobank.api.database.entities.Account;
import com.ecobank.api.database.entities.User;

import javax.swing.text.html.Option;
import java.util.Optional;

public interface IAccountService {
    Optional<Account> getAccountsByUserEmail(String email);
    Optional<Account> createAccountForUser(String email, String currency);

}
