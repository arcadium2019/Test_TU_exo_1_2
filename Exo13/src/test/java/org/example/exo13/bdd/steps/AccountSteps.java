package org.example.exo13.bdd.steps;

import io.cucumber.java.Before;
import io.cucumber.java.en.*;
import org.example.exo13.exception.InsufficientFundsException;
import org.example.exo13.model.BankAccount;
import org.example.exo13.repository.InMemoryAccountRepository;
import org.example.exo13.service.AccountService;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class AccountSteps {

    private AccountService service;
    private final Map<String, BankAccount> accounts = new HashMap<>();
    private Exception lastException;

    @Before
    public void setUp() {
        service = new AccountService(new InMemoryAccountRepository());
        accounts.clear();
        lastException = null;
    }

    @When("I create an account for {string}")
    public void createAccount(String holder) {
        BankAccount account = service.createAccount(holder);
        accounts.put(holder, account);
    }

    @Then("the account should exist with a balance of {int}")
    public void accountExistsWithBalance(int expectedBalance) {
        BankAccount account = accounts.values().iterator().next();
        assertNotNull(account);
        assertEquals(expectedBalance, account.getBalance(), 0.001);
    }

    @Given("an account for {string} with a balance of {int}")
    public void accountWithBalance(String holder, int balance) {
        BankAccount account = service.createAccount(holder);
        account.setBalance(balance);
        accounts.put(holder, account);
    }

    @When("I deposit {int} into the account of {string}")
    public void deposit(int amount, String holder) {
        BankAccount account = accounts.get(holder);
        BankAccount updated = service.deposit(account.getNumber(), amount);
        accounts.put(holder, updated);
    }

    @Then("the balance of {string} should be {int}")
    public void balanceShouldBe(String holder, int expectedBalance) {
        BankAccount account = accounts.get(holder);
        assertEquals(expectedBalance, account.getBalance(), 0.001);
    }

    @When("I withdraw {int} from the account of {string}")
    public void withdraw(int amount, String holder) {
        BankAccount account = accounts.get(holder);
        BankAccount updated = service.withdraw(account.getNumber(), amount);
        accounts.put(holder, updated);
    }

    @When("I try to withdraw {int} from the account of {string}")
    public void tryWithdraw(int amount, String holder) {
        try {
            BankAccount account = accounts.get(holder);
            service.withdraw(account.getNumber(), amount);
        } catch (Exception e) {
            lastException = e;
        }
    }

    @Then("the withdrawal should be rejected for insufficient funds")
    public void withdrawalRejectedInsufficientFunds() {
        assertNotNull(lastException);
        assertInstanceOf(InsufficientFundsException.class, lastException);
    }

    @When("I transfer {int} from {string} to {string}")
    public void transfer(int amount, String fromHolder, String toHolder) {
        BankAccount from = accounts.get(fromHolder);
        BankAccount to = accounts.get(toHolder);
        service.transfer(from.getNumber(), to.getNumber(), amount);
        accounts.put(fromHolder, service.findByNumber(from.getNumber()));
        accounts.put(toHolder, service.findByNumber(to.getNumber()));
    }

    @When("I try to transfer {int} from {string} to {string}")
    public void tryTransfer(int amount, String fromHolder, String toHolder) {
        try {
            BankAccount from = accounts.get(fromHolder);
            BankAccount to = accounts.get(toHolder);
            service.transfer(from.getNumber(), to.getNumber(), amount);
        } catch (Exception e) {
            lastException = e;
        }
    }

    @Then("the transfer should be rejected for insufficient funds")
    public void transferRejectedInsufficientFunds() {
        assertNotNull(lastException);
        assertInstanceOf(InsufficientFundsException.class, lastException);
    }
}
