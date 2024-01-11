package com.ecobank.api.models.user;

import com.ecobank.api.database.entities.Account;
import com.ecobank.api.database.entities.User;
import lombok.Data;
@Data
public class UserInfo {
    private String firstName;
    private String lastName;
    private String IBAN;

    public UserInfo(Account account) {
        this.firstName = account.getUser().getFirstName();
        this.lastName = account.getUser().getLastName();
        this.IBAN = account.getIBAN();
    }
}
