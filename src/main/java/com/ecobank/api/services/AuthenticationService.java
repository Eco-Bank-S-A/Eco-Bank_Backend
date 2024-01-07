package com.ecobank.api.services;

import com.ecobank.api.database.entities.User;
import com.ecobank.api.models.jwt.JwtTokenData;
import com.ecobank.api.services.abstractions.IAuthenticationService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthenticationService implements IAuthenticationService {

    private final JwtService jwtService;
    private final PasswordEncoder encoder;



    public AuthenticationService(JwtService jwtService, PasswordEncoder encoder) {
        this.jwtService = jwtService;
        this.encoder = encoder;
    }

    @Override
    public String createToken(User user) {
        return jwtService.generateToken(user);
    }

    @Override
    public Optional<JwtTokenData> getTokenData(String token) {
        var tokenData = jwtService.verifyToken(token);

        if(!tokenData.isVerified()) {
            return Optional.empty();
        }

        return Optional.of(tokenData);
    }

    @Override
    public String createPasswordHash(String password) {
        return encoder.encode(password);
    }

    @Override
    public boolean verifyPassword(String password, String hashedPassword) {
        return encoder.matches(password, hashedPassword);
    }
}
