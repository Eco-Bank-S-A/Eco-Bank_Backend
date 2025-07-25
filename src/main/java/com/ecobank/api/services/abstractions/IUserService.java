package com.ecobank.api.services.abstractions;

import com.ecobank.api.database.entities.User;
import com.ecobank.api.models.authentication.RegisterRequest;

import java.util.Optional;

public interface IUserService {

    Optional<User> registerUser(RegisterRequest request);

    Optional<User> getUserByEmail (String email);
    Optional<Object> getUserInfo(String email);

    void changeMaxCo2(User user, long maxCo2);
}
