package com.ecobank.api.database.repositories;

import com.ecobank.api.database.entities.Co2Stock;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Optional;

@Repository
public interface ICo2StockRepository extends CrudRepository<Co2Stock, Long> {

    Optional<Co2Stock> findById(long id);
    ArrayList<Co2Stock> findByCreatedAtAfterOrderByCreatedAt(LocalDateTime date);
}
