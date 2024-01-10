package com.ecobank.api.database.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
@Entity
@Table(name = "accounts")
public class Account {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    private String currency;
    private String IBAN;

    @Column(precision = 10, scale = 2)
    private BigDecimal balance;

    @Column(precision = 10, scale = 2)
    private BigDecimal freeFunds;
    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "company_id", referencedColumnName = "id")
    private Company company;
    @ManyToOne
    @JoinColumn(name = "account_type", referencedColumnName = "id")
    private AccountType  accountType;
}
