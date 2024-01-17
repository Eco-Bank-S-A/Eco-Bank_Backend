package com.ecobank.api.services;

import com.ecobank.api.Constants;
import com.ecobank.api.database.entities.Account;
import com.ecobank.api.database.entities.AccountType;
import com.ecobank.api.database.entities.User;
import com.ecobank.api.database.repositories.IAccountRepository;
import com.ecobank.api.database.repositories.IAccountTypeRepository;
import com.ecobank.api.database.repositories.IUserRepository;
import com.ecobank.api.services.abstractions.IAccountService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Optional;
import java.util.Random;

@Service
public class AccountService implements IAccountService {

    private final IUserRepository userRepository;
    private final IAccountRepository accountRepository;
    private final IAccountTypeRepository accountTypeRepository;
    public AccountService(IUserRepository userRepository, IAccountRepository accountRepository, IAccountTypeRepository accountTypeRepository) {
        this.userRepository = userRepository;
        this.accountRepository = accountRepository;
        this.accountTypeRepository = accountTypeRepository;
    }

    @Override
    public Optional<Account> getAccountsByUserEmail(String email) {
        var user = userRepository.findUserByEmail(email);
        if (user == null)
            return Optional.empty();
        return accountRepository.findAccountByUser(user);
    }

    @Override
    public Optional<Account> getAccountsByIBAN(String iban) {
        return accountRepository.findAccountByIBAN(iban);
    }

    @Override
    public Optional<Account> createAccountForUser(String email, String accountTypeName) {
        var user = userRepository.findUserByEmail(email);
        if (user.isEmpty())
            return Optional.empty();
        var accountType = accountTypeRepository.findAccountTypeByType(accountTypeName);
        if (accountType.isPresent())
            return Optional.empty();


        var account = Account.builder()
                .balance(new BigDecimal(0))
                .IBAN(generateIban())
                .currency("EURO")
                .freeFunds(new BigDecimal(0))
                .user(user.get())
                .accountType(accountType.get())
                .build();

        accountRepository.save(account);
        return Optional.of(account);

    }

    @Override
    public Account getBankAccount() {
        return accountRepository.findAccountByIBAN(Constants.BankAccountIBAN).get();
    }

    @Override
    public boolean tryChangeAmount(Account account, BigDecimal amount) {
        var currentFreeFunds = account.getFreeFunds();
        var currentBalance = account.getBalance();

        if (amount.compareTo(BigDecimal.ZERO) < 0 && currentFreeFunds.compareTo(amount.abs()) < 0) {
            return false;
        }

        account.setFreeFunds(currentFreeFunds.add(amount));
        account.setBalance(currentBalance.add(amount));
        accountRepository.save(account);
        return true;
    }


    public static String generateIban() {
        var accountNumber = generateRandomAccountNumber();
        BigInteger bigInt = new BigInteger(accountNumber);
        int checksum = 99 - bigInt.mod(BigInteger.valueOf(98)).intValue();//TODO VALIDATE CHECKSUM

        return "PL" + String.format("%02d", checksum) + Constants.BankCode + accountNumber;
    }

    private static String generateRandomAccountNumber() {
        StringBuilder accountNumber = new StringBuilder();
        Random random = new Random();

        accountNumber.append(random.nextInt(9) + 1);

        for (int i = 1; i < 16; i++) {
            accountNumber.append(random.nextInt(10));
        }

        return accountNumber.toString();
    }
}