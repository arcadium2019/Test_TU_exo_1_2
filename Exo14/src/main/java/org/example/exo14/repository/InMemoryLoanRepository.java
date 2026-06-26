package org.example.exo14.repository;

import org.example.exo14.model.Loan;

import java.util.*;
import java.util.stream.Collectors;

public class InMemoryLoanRepository implements LoanRepository {
    private final Map<String, Loan> store = new HashMap<>();

    @Override
    public Loan save(Loan loan) {
        store.put(loan.getId(), loan);
        return loan;
    }

    @Override
    public Optional<Loan> findById(String id) {
        return Optional.ofNullable(store.get(id));
    }

    @Override
    public List<Loan> findActiveByBookId(String bookId) {
        return store.values().stream()
                .filter(l -> l.getBookId().equals(bookId) && l.getReturnDate() == null)
                .collect(Collectors.toList());
    }
}
