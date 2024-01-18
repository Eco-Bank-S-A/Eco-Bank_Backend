package com.ecobank.api.database.entities;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "companies")
public class Company {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    private String name;
    private String regon;
    private String nip;
    private String krs;
    private String callbackUrl;

    private int CO2Factor;
}
