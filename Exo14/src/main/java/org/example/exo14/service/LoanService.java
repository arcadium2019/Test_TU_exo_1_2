package org.example.exo14.service;

import org.example.exo14.exception.BookUnavailableException;
import org.example.exo14.exception.LoanNotFoundException;
import org.example.exo14.exception.MemberSuspendedException;
import org.example.exo14.model.Book;
import org.example.exo14.model.Loan;
import org.example.exo14.model.Member;
import org.example.exo14.repository.BookRepository;
import org.example.exo14.repository.LoanRepository;
import org.example.exo14.repository.MemberRepository;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class LoanService {

    private static final double PENALTY_PER_DAY = 0.15;

    private final LoanRepository loanRepository;
    private final BookRepository bookRepository;
    private final MemberRepository memberRepository;

    public LoanService(LoanRepository loanRepository,
                       BookRepository bookRepository,
                       MemberRepository memberRepository) {
        this.loanRepository = loanRepository;
        this.bookRepository = bookRepository;
        this.memberRepository = memberRepository;
    }

    public Loan createLoan(String memberId, String bookId, LocalDate loanDate) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("Member not found: " + memberId));
        if (member.isSuspended()) {
            throw new MemberSuspendedException("Member " + memberId + " is suspended");
        }

        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new IllegalArgumentException("Book not found: " + bookId));
        if (!book.isAvailable()) {
            throw new BookUnavailableException("Book " + bookId + " is not available");
        }

        book.setAvailable(false);
        bookRepository.save(book);

        return loanRepository.save(new Loan(memberId, bookId, loanDate));
    }

    public Loan returnBook(String loanId, LocalDate returnDate) {
        Loan loan = loanRepository.findById(loanId)
                .orElseThrow(() -> new LoanNotFoundException("Loan not found: " + loanId));

        loan.setReturnDate(returnDate);

        long daysLate = ChronoUnit.DAYS.between(loan.getDueDate(), returnDate);
        if (daysLate > 0) {
            loan.setPenalty(daysLate * PENALTY_PER_DAY);
            Member member = memberRepository.findById(loan.getMemberId()).orElseThrow();
            member.incrementLateReturnCount();
            memberRepository.save(member);
        }

        Book book = bookRepository.findById(loan.getBookId()).orElseThrow();
        book.setAvailable(true);
        bookRepository.save(book);

        return loanRepository.save(loan);
    }
}
