package com.ecobank.api.models.account;

import lombok.Data;

@Data

public class NewAccountRequest {
    private String email;
    private String accountType;
}
