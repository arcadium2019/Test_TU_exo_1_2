package org.example.exo13.repository;

import org.example.exo13.model.BankAccount;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public class InMemoryAccountRepository implements AccountRepository {

    private final Map<String, BankAccount> store = new LinkedHashMap<>();

    @Override
    public BankAccount save(BankAccount account) {
        store.put(account.getNumber(), account);
        return account;
    }

    @Override
    public Optional<BankAccount> findByNumber(String number) {
        return Optional.ofNullable(store.get(number));
    }

    @Override
    public List<BankAccount> findAll() {
        return new ArrayList<>(store.values());
    }

    @Override
    public boolean existsByNumber(String number) {
        return store.containsKey(number);
    }
}
