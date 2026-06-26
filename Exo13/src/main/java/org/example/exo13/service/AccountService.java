package org.example.exo13.service;

import org.example.exo13.exception.AccountAlreadyExistsException;
import org.example.exo13.exception.AccountNotFoundException;
import org.example.exo13.exception.InsufficientFundsException;
import org.example.exo13.exception.InvalidAmountException;
import org.example.exo13.model.BankAccount;
import org.example.exo13.repository.AccountRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class AccountService {

    private final AccountRepository repository;

    public AccountService(AccountRepository repository) {
        this.repository = repository;
    }

    public BankAccount createAccount(String holder) {
        String number = UUID.randomUUID().toString();
        if (repository.existsByNumber(number)) {
            throw new AccountAlreadyExistsException("Account number already exists: " + number);
        }
        return repository.save(new BankAccount(number, holder));
    }

    public BankAccount findByNumber(String number) {
        return repository.findByNumber(number)
                .orElseThrow(() -> new AccountNotFoundException("Account not found: " + number));
    }

    public List<BankAccount> findAll() {
        return repository.findAll();
    }

    public BankAccount deposit(String number, double amount) {
        if (amount <= 0) {
            throw new InvalidAmountException("Deposit amount must be strictly positive");
        }
        BankAccount account = findByNumber(number);
        account.setBalance(account.getBalance() + amount);
        return repository.save(account);
    }

    public BankAccount withdraw(String number, double amount) {
        if (amount <= 0) {
            throw new InvalidAmountException("Withdraw amount must be strictly positive");
        }
        BankAccount account = findByNumber(number);
        if (account.getBalance() < amount) {
            throw new InsufficientFundsException("Insufficient funds: balance is " + account.getBalance());
        }
        account.setBalance(account.getBalance() - amount);
        return repository.save(account);
    }

    public void transfer(String fromNumber, String toNumber, double amount) {
        if (amount <= 0) {
            throw new InvalidAmountException("Transfer amount must be strictly positive");
        }
        BankAccount from = findByNumber(fromNumber);
        BankAccount to = findByNumber(toNumber);
        if (from.getBalance() < amount) {
            throw new InsufficientFundsException("Insufficient funds for transfer: balance is " + from.getBalance());
        }
        from.setBalance(from.getBalance() - amount);
        to.setBalance(to.getBalance() + amount);
        repository.save(from);
        repository.save(to);
    }
}
