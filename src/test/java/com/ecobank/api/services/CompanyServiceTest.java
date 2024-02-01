package com.ecobank.api.services;

import com.ecobank.api.database.entities.Company;
import com.ecobank.api.database.repositories.ICompanyRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.stubbing.OngoingStubbing;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class CompanyServiceTest {

    @Mock
    private ICompanyRepository companyRepository;

    @InjectMocks
    private CompanyService companyService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void calculateCO2() {
        Company company = mock(Company.class);
        BigDecimal amount = BigDecimal.valueOf(1000);
        when(company.getCO2Factor()).thenReturn(10);
        Long calculatedCO2 = companyService.calculateCO2(company, amount);
        Long expectedCO2 = 10000L;
        assertEquals(expectedCO2, calculatedCO2, "The calculated CO2 amount should be correct");

        verify(company, times(1)).getCO2Factor();
    }
}
