package com.ecobank.api.services;

import com.ecobank.api.database.entities.User;
import com.ecobank.api.database.repositories.IUserRepository;
import com.ecobank.api.models.authentication.RegisterRequest;
import com.ecobank.api.services.abstractions.IUserService;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService implements IUserService {

    private final IUserRepository repository;

    public UserService(IUserRepository repository) {
        this.repository = repository;
    }


    @Override
    public Optional<User> registerUser(RegisterRequest request) {
        var presentUser = repository.findUserByEmailAndPesel(request.getEmail(), request.getPesel());
        if (presentUser.isPresent()) {
            return Optional.empty();
        }

        var user = User.builder()
                .firstName(request.getFirstname())
                .lastName(request.getLastname())
                .pesel(request.getPesel())
                .phone(request.getPhone())
                .email(request.getEmail())
                .password(request.getPassword())
                .CO2(0L)
                .maxCO2(1200L)
                .build();

        repository.save(user);
        return Optional.of(user);
    }

    public Optional<User> getUserByEmail (String email) {
        return repository.findUserByEmail(email);
    }
}
