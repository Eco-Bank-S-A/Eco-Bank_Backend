package com.ecobank.api.models.authentication;

import lombok.Data;

@Data
public class AuthenticateRequest {
    private String email;
    private String password;
}
