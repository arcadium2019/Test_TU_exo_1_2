package org.example.exo13.repository;

import org.example.exo13.model.BankAccount;

import java.util.List;
import java.util.Optional;

public interface AccountRepository {
    BankAccount save(BankAccount account);
    Optional<BankAccount> findByNumber(String number);
    List<BankAccount> findAll();
    boolean existsByNumber(String number);
}
