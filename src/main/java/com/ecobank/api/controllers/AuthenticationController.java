package com.ecobank.api.controllers;

import com.ecobank.api.models.authentication.AuthenticateRequest;
import com.ecobank.api.models.authentication.AuthenticationResponse;
import com.ecobank.api.models.authentication.RegisterRequest;
import com.ecobank.api.models.authentication.RenewTokenRequest;
import com.ecobank.api.services.AuthenticationService;
import com.ecobank.api.services.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthenticationController {

    private final AuthenticationService authenticationService;
    private final UserService userService;

    public AuthenticationController(AuthenticationService authenticationService, UserService userService) {
        this.authenticationService = authenticationService;
        this.userService = userService;
    }


    @PostMapping("/register")
    public ResponseEntity<AuthenticationResponse> register(@RequestBody RegisterRequest request) {
        var hashedPassword = authenticationService.createPasswordHash(request.getPassword());
        request.setPassword(hashedPassword);

        var user = userService.registerUser(request);
        if (user.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        var response = new AuthenticationResponse(authenticationService.createToken(user.get()));

        return ResponseEntity.ok(response);
    }

    @PostMapping("/authenticate")
    public ResponseEntity<AuthenticationResponse> authenticate(@RequestBody AuthenticateRequest request) {
        var user = userService.getUserByEmail(request.getEmail());

        if(user.isEmpty() || !authenticationService.verifyPassword(request.getPassword(), user.get().getPassword())) {
            return ResponseEntity.badRequest().build();
        }

        var response = new AuthenticationResponse(authenticationService.createToken(user.get()));

        return ResponseEntity.ok(response);
    }

    @GetMapping("/renew-token")
    public ResponseEntity<AuthenticationResponse> authenticate() {
        var token = SecurityContextHolder.getContext().getAuthentication();

        var user = userService.getUserByEmail(token.getName());
        var response = new AuthenticationResponse(authenticationService.createToken(user.get()));

        return ResponseEntity.ok(response);
    }
}
