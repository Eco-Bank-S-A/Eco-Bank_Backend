package com.ecobank.api.database.repositories;

import com.ecobank.api.database.entities.Account;
import com.ecobank.api.database.entities.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.Optional;

@Repository
public interface IAccountRepository extends CrudRepository<Account, Long> {
    Optional<Account> findAccountByUser(Optional<User> user);
    Optional<Account> findAccountByIBAN(String iban);
}
