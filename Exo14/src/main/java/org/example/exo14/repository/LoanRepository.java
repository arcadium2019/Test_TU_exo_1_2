package org.example.exo14.repository;

import org.example.exo14.model.Loan;

import java.util.List;
import java.util.Optional;

public interface LoanRepository {
    Loan save(Loan loan);
    Optional<Loan> findById(String id);
    List<Loan> findActiveByBookId(String bookId);
}
