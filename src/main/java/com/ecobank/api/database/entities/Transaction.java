package com.ecobank.api.database.entities;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Entity
@Data
@Table(name = "transactions")
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @Column(unique = true)
    private String uuid;
    private String additionalInfo;

    @ManyToOne
    private Account sender;

    @ManyToOne
    private Account receiver;

    private int status;

    @Column(precision = 10, scale = 2)
    private BigDecimal balance;

    private Long CO2;
}
