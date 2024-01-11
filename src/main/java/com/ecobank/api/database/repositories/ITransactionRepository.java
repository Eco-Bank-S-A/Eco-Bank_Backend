package com.ecobank.api.database.repositories;

import com.ecobank.api.database.entities.Transaction;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ITransactionRepository extends CrudRepository<Transaction, Long> {
    @Query("SELECT t FROM Transaction t " +
            "WHERE t.sender.user.email = :userEmail " +
            "AND t.status IN :operationTypes " +
            "AND t.creationDate BETWEEN :from AND :to " + // Added space before "ORDER BY"
            "ORDER BY t.creationDate DESC")
    List<Transaction> findTransactionHistory(@Param("userEmail") String userEmail,
                                             @Param("operationTypes") int[] operationTypes,
                                             @Param("from") LocalDateTime from,
                                             @Param("to") LocalDateTime to,
                                             Pageable pageable);

    @Query("SELECT COUNT(t) FROM Transaction t " +
            "WHERE t.sender.user.email = :userEmail " +
            "AND t.status IN :operationTypes " +
            "AND t.creationDate BETWEEN :from AND :to")
    int countTransactionHistory(@Param("userEmail") String userEmail,
                                @Param("operationTypes") int[] operationTypes,
                                @Param("from") LocalDateTime from,
                                @Param("to") LocalDateTime to);
}
