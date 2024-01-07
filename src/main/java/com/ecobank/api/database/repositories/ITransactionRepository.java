package com.ecobank.api.database.repositories;

import com.ecobank.api.database.entities.Transaction;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ITransactionRepository extends CrudRepository<Transaction, Long> {
}
