package com.ecobank.api.database.repositories;

import com.ecobank.api.database.entities.Account;
import com.ecobank.api.database.entities.AccountType;
import com.ecobank.api.database.entities.User;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface IAccountTypeRepository extends CrudRepository<AccountType, Long> {
    Optional<AccountType> findAccountTypeByType(String type);

}
