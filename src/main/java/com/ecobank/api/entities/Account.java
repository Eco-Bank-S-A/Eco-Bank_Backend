package com.ecobank.api.entities;

import jakarta.persistence.*;

import java.math.BigDecimal;

@Entity
@Table(name = "accounts")
public class Account {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    private String currency;

    @Column(precision = 10, scale = 2)
    private BigDecimal balance;

    @Column(precision = 10, scale = 2)
    private BigDecimal freeFunds;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;

    @OneToOne(cascade = CascadeType.ALL, optional = true)
    @JoinColumn(name = "company_id", referencedColumnName = "id")
    private Company company;
}
