package com.ecobank.api.controllers;

import com.ecobank.api.models.authentication.AuthenticateRequest;
import com.ecobank.api.models.authentication.AuthenticationResponse;
import com.ecobank.api.models.authentication.RegisterRequest;
import com.ecobank.api.models.authentication.RenewTokenRequest;
import com.ecobank.api.services.AuthenticationService;
import com.ecobank.api.services.Co2StockRateService;
import com.ecobank.api.services.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationProvider;
import org.springframework.security.oauth2.server.resource.web.authentication.BearerTokenAuthenticationFilter;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
public class AuthenticationController {

    private final AuthenticationService authenticationService;
    private final UserService userService;
    private final Co2StockRateService co2StockRateService;

    public AuthenticationController(AuthenticationService authenticationService, UserService userService, Co2StockRateService co2StockRateService) {
        this.authenticationService = authenticationService;
        this.userService = userService;
        this.co2StockRateService = co2StockRateService;
    }


    @GetMapping("/test")
    public ResponseEntity<Optional<Double>> test() {
//        var co2 = co2StockRateService.getCo2StockRate();
//        return ResponseEntity.ok(co2);
        return ResponseEntity.ok(Optional.empty());
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

//        BearerTokenAuthenticationFilter
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
