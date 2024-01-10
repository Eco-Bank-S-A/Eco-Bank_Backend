package com.ecobank.api.database.entities;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "accounts_type")
public class AccountType {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;
    @Column(unique = true)
    private String type;
}