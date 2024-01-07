package com.ecobank.api.database.repositories;

import com.ecobank.api.database.entities.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface IUserRepository extends CrudRepository<User, Long> {

    Optional<User> findUserByEmailAndPesel(String emial, String pesel);

    Optional<User> findUserByEmail(String emial);

}
