package com.ecobank.api.entities;

import jakarta.persistence.*;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    private String firstName;
    private String lastName;
    private String pesel;


    private String email;
    private String password;


    private Long CO2;
}
