package com.ecobank.api.database.repositories;

import com.ecobank.api.database.entities.Co2Stock;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ICo2StockRepository extends CrudRepository<Co2Stock, Long> {
}
