package org.example.exo13.controller;

import jakarta.validation.Valid;
import org.example.exo13.dto.AmountRequest;
import org.example.exo13.dto.CreateAccountRequest;
import org.example.exo13.dto.TransferRequest;
import org.example.exo13.model.BankAccount;
import org.example.exo13.service.AccountService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/accounts")
public class AccountController {

    private final AccountService service;

    public AccountController(AccountService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<BankAccount> create(@Valid @RequestBody CreateAccountRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.createAccount(request.getHolder()));
    }

    @GetMapping
    public List<BankAccount> findAll() {
        return service.findAll();
    }

    @GetMapping("/{number}")
    public BankAccount findByNumber(@PathVariable("number") String number) {
        return service.findByNumber(number);
    }

    @PostMapping("/{number}/deposit")
    public BankAccount deposit(@PathVariable("number") String number, @RequestBody AmountRequest request) {
        return service.deposit(number, request.getAmount());
    }

    @PostMapping("/{number}/withdraw")
    public BankAccount withdraw(@PathVariable("number") String number, @RequestBody AmountRequest request) {
        return service.withdraw(number, request.getAmount());
    }

    @PostMapping("/transfer")
    public ResponseEntity<Void> transfer(@RequestBody TransferRequest request) {
        service.transfer(request.getFromNumber(), request.getToNumber(), request.getAmount());
        return ResponseEntity.ok().build();
    }
}
