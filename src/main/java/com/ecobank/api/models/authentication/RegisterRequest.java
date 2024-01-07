package com.ecobank.api.models.authentication;

import lombok.Data;

@Data
public class RegisterRequest {
    private String firstname;
    private String lastname;
    private String pesel;
    private String phone;

    private String email;
    private String password;
}
