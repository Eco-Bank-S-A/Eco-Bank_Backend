package com.ecobank.api.database.repositories;

import com.ecobank.api.database.entities.Co2Stock;
import com.ecobank.api.database.entities.Company;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ICompanyRepository extends CrudRepository<Company, Long> {
    Optional<Company> findById(long id);

}
