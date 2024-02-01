package com.ecobank.api.services;

import com.ecobank.api.database.entities.Account;
import com.ecobank.api.database.entities.Transaction;
import com.ecobank.api.database.enums.TransactionStatus;
import com.ecobank.api.database.repositories.ITransactionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TransferServiceTest {

    @Mock
    private AccountService accountService;
    @Mock
    private ITransactionRepository transactionRepository;
    @InjectMocks
    private TransferService transferService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void transferFromBank_success() {
        String userEmail = "user@example.com";
        String title = "Transfer Title";
        BigDecimal amount = BigDecimal.valueOf(100);

        Account userAccount = new Account();
        Account bankAccount = new Account();

        when(accountService.getAccountsByUserEmail(userEmail)).thenReturn(Optional.of(userAccount));
        when(accountService.getBankAccount()).thenReturn(bankAccount);
        when(accountService.tryChangeAmount(eq(userAccount), any(BigDecimal.class))).thenReturn(true);

        boolean result = transferService.transferFromBank(userEmail, title, amount);

        assertTrue(result, "Transfer should be successful");
        verify(transactionRepository, times(1)).save(any(Transaction.class));
    }

    @Test
    void transferFromBank_accountNotFound() {
        String userEmail = "nonexistent@example.com";
        BigDecimal amount = BigDecimal.valueOf(100);

        when(accountService.getAccountsByUserEmail(userEmail)).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () -> transferService.transferFromBank(userEmail, null, amount),
                "Should throw NoSuchElementException if account is not found");
    }
    @Test
    void tryTransferMoney_success() {
        Account sender = new Account();
        Account recipient = new Account();
        BigDecimal amount = BigDecimal.valueOf(50);

        when(accountService.tryChangeAmount(sender, amount.negate())).thenReturn(true);
        when(accountService.tryChangeAmount(recipient, amount)).thenReturn(true);

        boolean result = transferService.tryTransferMoney(sender, recipient, amount);

        assertTrue(result, "Transfer should be successful");
    }

    @Test
    void tryTransferMoney_failure() {
        Account sender = new Account();
        Account recipient = new Account();
        BigDecimal amount = BigDecimal.valueOf(50);

        when(accountService.tryChangeAmount(sender, amount.negate())).thenReturn(true);
        when(accountService.tryChangeAmount(recipient, amount)).thenReturn(false);

        boolean result = transferService.tryTransferMoney(sender, recipient, amount);

        assertFalse(result, "Transfer should fail if recipient account update fails");
    }

}
