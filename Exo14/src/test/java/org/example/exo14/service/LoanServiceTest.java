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
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LoanServiceTest {

    @Mock private LoanRepository loanRepository;
    @Mock private BookRepository bookRepository;
    @Mock private MemberRepository memberRepository;

    @InjectMocks
    private LoanService service;

    private static final LocalDate LOAN_DATE = LocalDate.of(2026, 1, 1);

    // ---- Création d'un prêt ----

    @Test
    void create_loan_sets_due_date_21_days_after_loan_date() {
        // Arrange
        Member member = new Member("M1", "Alice");
        Book book = new Book("B1", "Clean Code");
        when(memberRepository.findById("M1")).thenReturn(Optional.of(member));
        when(bookRepository.findById("B1")).thenReturn(Optional.of(book));
        when(loanRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        // Act
        Loan loan = service.createLoan("M1", "B1", LOAN_DATE);

        // Assert
        assertThat(loan.getDueDate()).isEqualTo(LOAN_DATE.plusDays(21));
    }

    @Test
    void create_loan_marks_book_as_unavailable() {
        // Arrange
        Member member = new Member("M1", "Alice");
        Book book = new Book("B1", "Clean Code");
        when(memberRepository.findById("M1")).thenReturn(Optional.of(member));
        when(bookRepository.findById("B1")).thenReturn(Optional.of(book));
        when(loanRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
        when(bookRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        // Act
        service.createLoan("M1", "B1", LOAN_DATE);

        // Assert
        assertThat(book.isAvailable()).isFalse();
    }

    @Test
    void create_loan_for_unavailable_book_throws_book_unavailable() {
        // Arrange
        Member member = new Member("M1", "Bob");
        Book book = new Book("B2", "Refactoring");
        book.setAvailable(false);
        when(memberRepository.findById("M1")).thenReturn(Optional.of(member));
        when(bookRepository.findById("B2")).thenReturn(Optional.of(book));

        // Act & Assert
        assertThatThrownBy(() -> service.createLoan("M1", "B2", LOAN_DATE))
                .isInstanceOf(BookUnavailableException.class);
    }

    @Test
    void create_loan_for_suspended_member_throws_member_suspended() {
        // Arrange
        Member member = new Member("M2", "Carol");
        member.setSuspended(true);
        when(memberRepository.findById("M2")).thenReturn(Optional.of(member));

        // Act & Assert
        assertThatThrownBy(() -> service.createLoan("M2", "B1", LOAN_DATE))
                .isInstanceOf(MemberSuspendedException.class);
    }

    // ---- Retour et pénalités ----

    @Test
    void return_on_time_has_zero_penalty() {
        // Arrange
        Member member = new Member("M1", "Dave");
        Book book = new Book("B1", "Clean Code");
        book.setAvailable(false);
        Loan loan = new Loan("M1", "B1", LOAN_DATE);
        LocalDate onTime = loan.getDueDate();

        when(loanRepository.findById(loan.getId())).thenReturn(Optional.of(loan));
        when(bookRepository.findById("B1")).thenReturn(Optional.of(book));
        when(loanRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
        when(bookRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        // Act
        Loan result = service.returnBook(loan.getId(), onTime);

        // Assert
        assertThat(result.getPenalty()).isEqualTo(0.0);
    }

    @Test
    void return_late_calculates_penalty_at_0_15_per_day() {
        // Arrange
        Member member = new Member("M1", "Eve");
        Book book = new Book("B1", "Design Patterns");
        book.setAvailable(false);
        Loan loan = new Loan("M1", "B1", LOAN_DATE);
        LocalDate returnDate = loan.getDueDate().plusDays(10);

        when(loanRepository.findById(loan.getId())).thenReturn(Optional.of(loan));
        when(memberRepository.findById("M1")).thenReturn(Optional.of(member));
        when(bookRepository.findById("B1")).thenReturn(Optional.of(book));
        when(loanRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
        when(memberRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
        when(bookRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        // Act
        Loan result = service.returnBook(loan.getId(), returnDate);

        // Assert
        assertThat(result.getPenalty()).isEqualTo(10 * 0.15, within(0.001));
    }

    @Test
    void late_return_increments_member_late_return_count() {
        // Arrange
        Member member = new Member("M1", "Frank");
        Book book = new Book("B1", "DDD");
        book.setAvailable(false);
        Loan loan = new Loan("M1", "B1", LOAN_DATE);
        LocalDate late = loan.getDueDate().plusDays(5);

        when(loanRepository.findById(loan.getId())).thenReturn(Optional.of(loan));
        when(memberRepository.findById("M1")).thenReturn(Optional.of(member));
        when(bookRepository.findById("B1")).thenReturn(Optional.of(book));
        when(loanRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
        when(memberRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
        when(bookRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        // Act
        service.returnBook(loan.getId(), late);

        // Assert
        assertThat(member.getLateReturnCount()).isEqualTo(1);
    }

    @Test
    void three_late_returns_suspend_member() {
        // Arrange
        Member member = new Member("M1", "Grace");

        // Act — simulate 3 late returns directly on the member
        member.incrementLateReturnCount();
        member.incrementLateReturnCount();
        member.incrementLateReturnCount();

        // Assert
        assertThat(member.isSuspended()).isTrue();
    }

    @Test
    void return_book_makes_it_available_again() {
        // Arrange
        Member member = new Member("M1", "Henry");
        Book book = new Book("B1", "SICP");
        book.setAvailable(false);
        Loan loan = new Loan("M1", "B1", LOAN_DATE);

        when(loanRepository.findById(loan.getId())).thenReturn(Optional.of(loan));
        when(bookRepository.findById("B1")).thenReturn(Optional.of(book));
        when(loanRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
        when(bookRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        // Act
        service.returnBook(loan.getId(), loan.getDueDate());

        // Assert
        assertThat(book.isAvailable()).isTrue();
    }

    @Test
    void return_unknown_loan_throws_loan_not_found() {
        // Arrange
        when(loanRepository.findById("unknown")).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> service.returnBook("unknown", LOAN_DATE))
                .isInstanceOf(LoanNotFoundException.class);
    }
}
