package com.ecobank.api.database.entities;

import com.ecobank.api.database.enums.TransactionStatus;
import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

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

    @Enumerated(EnumType.ORDINAL)
    private TransactionStatus status;

    @Column(precision = 10, scale = 2)
    private BigDecimal balance;

    private Long CO2;

    @Column(name = "created_at", columnDefinition = "TIMESTAMP")
    private LocalDateTime createdAt;
}
