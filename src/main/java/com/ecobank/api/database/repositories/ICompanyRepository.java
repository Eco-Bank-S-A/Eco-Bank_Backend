package com.ecobank.api.database.repositories;

import com.ecobank.api.database.entities.Company;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ICompanyRepository extends CrudRepository<Company, Long> {
}
