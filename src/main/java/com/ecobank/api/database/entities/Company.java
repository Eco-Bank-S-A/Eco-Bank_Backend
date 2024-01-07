package com.ecobank.api.database.entities;

import jakarta.persistence.*;

@Entity
@Table(name = "companies")
public class Company {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    private String name;
    private String regon;
    private String nip;
    private String krs;

    private int CO2Factor;
}
