package com.ecobank.api.services.abstractions;

import com.ecobank.api.database.entities.User;
import com.ecobank.api.models.jwt.JwtTokenData;

import java.util.Optional;

public interface IAuthenticationService {
    String createToken(User user);

    Optional<JwtTokenData> getTokenData(String token);

    String createPasswordHash(String password);
    boolean verifyPassword(String password, String hashedPassword);
}
