package com.ecobank.api.services;

import com.ecobank.api.database.entities.Account;
import com.ecobank.api.database.repositories.IAccountRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AccountServiceTest {

    @Mock
    private IAccountRepository accountRepository;

    @InjectMocks
    private AccountService accountService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void tryChangeAmount_AddFunds_Success() {
        Account account = new Account();
        account.setBalance(new BigDecimal("100.00"));
        account.setFreeFunds(new BigDecimal("100.00"));
        BigDecimal amountToAdd = new BigDecimal("50.00");

        boolean result = accountService.tryChangeAmount(account, amountToAdd);

        assertTrue(result, "The operation should succeed.");
        assertEquals(new BigDecimal("150.00"), account.getBalance(), "The balance should be correctly updated.");
        assertEquals(new BigDecimal("150.00"), account.getFreeFunds(), "The free funds should be correctly updated.");
        verify(accountRepository, times(1)).save(account);
    }

    @Test
    void tryChangeAmount_SubtractFunds_Success() {
        Account account = new Account();
        account.setBalance(new BigDecimal("100.00"));
        account.setFreeFunds(new BigDecimal("100.00"));
        BigDecimal amountToSubtract = new BigDecimal("-50.00");
        boolean result = accountService.tryChangeAmount(account, amountToSubtract);

        assertTrue(result, "The operation should succeed.");
        assertEquals(new BigDecimal("50.00"), account.getBalance(), "The balance should be correctly updated.");
        assertEquals(new BigDecimal("50.00"), account.getFreeFunds(), "The free funds should be correctly updated.");
    }

    @Test
    void tryChangeAmount_InsufficientFunds_Failure() {
        Account account = new Account();
        account.setBalance(new BigDecimal("100.00"));
        account.setFreeFunds(new BigDecimal("50.00"));
        BigDecimal amountToSubtract = new BigDecimal("-60.00");

        boolean result = accountService.tryChangeAmount(account, amountToSubtract);

        assertFalse(result, "The operation should fail due to insufficient funds.");
        assertEquals(new BigDecimal("100.00"), account.getBalance(), "The balance should not change.");
        assertEquals(new BigDecimal("50.00"), account.getFreeFunds(), "The free funds should not change.");
        verify(accountRepository, never()).save(account);
    }
}
