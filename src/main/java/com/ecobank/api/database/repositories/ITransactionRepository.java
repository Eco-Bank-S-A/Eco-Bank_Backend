package com.ecobank.api.database.repositories;

import com.ecobank.api.database.entities.Transaction;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Optional;

@Repository
public interface ITransactionRepository extends CrudRepository<Transaction, Long> {

    public Optional<Transaction> findTransactionByUuid(String uuid);
    @Query("SELECT t FROM Transaction t WHERE t.status = 1 AND (t.sender.id = ?1 OR t.receiver.id = ?1) ORDER BY t.id DESC")
    public ArrayList<Transaction> findFinalizedTransactionsByAccount(Long id);
}
