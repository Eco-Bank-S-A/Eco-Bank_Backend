package com.ecobank.api.controllers;

import com.ecobank.api.services.AuthenticationService;
import com.ecobank.api.services.UserService;
import com.ecobank.api.services.abstractions.IAuthenticationService;
import com.ecobank.api.services.abstractions.IUserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/user")
public class UserController {

    private final IAuthenticationService authenticationService;
    private final IUserService userService;

    public UserController(IAuthenticationService authenticationService, IUserService userService) {
        this.authenticationService = authenticationService;
        this.userService = userService;
    }

    @GetMapping("/me")
    public Object Me() {
        var token = SecurityContextHolder.getContext().getAuthentication();

        var user = userService.getUserByEmail(token.getName());
        if(user.isEmpty())
            return ResponseEntity.notFound();
        var userInfo = userService.getUserInfo(token.getName());
        return ResponseEntity.ok(userInfo);
    }
}

