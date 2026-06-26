package org.example.exo14.bdd.steps;

import io.cucumber.java.Before;
import io.cucumber.java.en.*;
import org.example.exo14.exception.MemberSuspendedException;
import org.example.exo14.model.Book;
import org.example.exo14.model.Loan;
import org.example.exo14.model.Member;
import org.example.exo14.model.Reservation;
import org.example.exo14.repository.*;
import org.example.exo14.service.LoanService;
import org.example.exo14.service.ReservationService;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class LibrarySteps {

    private InMemoryBookRepository bookRepository;
    private InMemoryMemberRepository memberRepository;
    private InMemoryLoanRepository loanRepository;
    private InMemoryReservationRepository reservationRepository;
    private LoanService loanService;
    private ReservationService reservationService;

    private final Map<String, Book> books = new HashMap<>();
    private final Map<String, Member> members = new HashMap<>();
    private final Map<String, Loan> loans = new HashMap<>();

    private Exception lastException;
    private Loan lastLoan;

    @Before
    public void setUp() {
        bookRepository = new InMemoryBookRepository();
        memberRepository = new InMemoryMemberRepository();
        loanRepository = new InMemoryLoanRepository();
        reservationRepository = new InMemoryReservationRepository();
        loanService = new LoanService(loanRepository, bookRepository, memberRepository);
        reservationService = new ReservationService(reservationRepository, memberRepository);
        books.clear();
        members.clear();
        loans.clear();
        lastException = null;
        lastLoan = null;
    }

    // ---- Given ----

    @Given("a book {string} is currently borrowed")
    public void bookIsBorrowed(String title) {
        Book book = new Book(title, title);
        book.setAvailable(false);
        bookRepository.save(book);
        books.put(title, book);
    }

    @Given("a book {string} exists and is available")
    public void bookExistsAvailable(String title) {
        Book book = new Book(title, title);
        bookRepository.save(book);
        books.put(title, book);
    }

    @Given("a member {string} is registered")
    public void memberIsRegistered(String name) {
        Member member = new Member(name, name);
        memberRepository.save(member);
        members.put(name, member);
    }

    @Given("a member {string} is registered and suspended")
    public void memberIsRegisteredAndSuspended(String name) {
        Member member = new Member(name, name);
        member.setSuspended(true);
        memberRepository.save(member);
        members.put(name, member);
    }

    @Given("{string} has borrowed {string}")
    public void memberHasBorrowed(String memberName, String bookTitle) {
        Member member = members.get(memberName);
        Book book = books.get(bookTitle);
        book.setAvailable(false);
        bookRepository.save(book);
        Loan loan = new Loan(member.getId(), book.getId(), LocalDate.of(2026, 1, 1));
        loanRepository.save(loan);
        loans.put(memberName + "-" + bookTitle, loan);
    }

    @Given("{string} has reserved {string}")
    public void memberHasReserved(String memberName, String bookTitle) {
        Member member = members.get(memberName);
        Book book = books.get(bookTitle);
        reservationService.createReservation(member.getId(), book.getId(), LocalDate.of(2026, 1, 2));
    }

    // ---- When ----

    @When("{string} reserves {string}")
    public void memberReserves(String memberName, String bookTitle) {
        Member member = members.get(memberName);
        Book book = books.get(bookTitle);
        reservationService.createReservation(member.getId(), book.getId(), LocalDate.now());
    }

    @When("{string} tries to reserve {string}")
    public void memberTriesToReserve(String memberName, String bookTitle) {
        try {
            Member member = members.get(memberName);
            Book book = books.get(bookTitle);
            reservationService.createReservation(member.getId(), book.getId(), LocalDate.now());
        } catch (Exception e) {
            lastException = e;
        }
    }

    @When("{string} returns {string}")
    public void memberReturns(String memberName, String bookTitle) {
        Loan loan = loans.get(memberName + "-" + bookTitle);
        loanService.returnBook(loan.getId(), LocalDate.of(2026, 1, 22));
    }

    @When("{string} borrows {string} on {string}")
    public void memberBorrowsOn(String memberName, String bookTitle, String dateStr) {
        Member member = members.get(memberName);
        Book book = books.get(bookTitle);
        LocalDate date = LocalDate.parse(dateStr);
        lastLoan = loanService.createLoan(member.getId(), book.getId(), date);
        loans.put(memberName + "-" + bookTitle, lastLoan);
    }

    @When("{string} returns {string} on {string}")
    public void memberReturnsOn(String memberName, String bookTitle, String dateStr) {
        Loan loan = loans.get(memberName + "-" + bookTitle);
        lastLoan = loanService.returnBook(loan.getId(), LocalDate.parse(dateStr));
    }

    // ---- Then ----

    @Then("the reservation for {string} on {string} is confirmed")
    public void reservationIsConfirmed(String memberName, String bookTitle) {
        Member member = members.get(memberName);
        Book book = books.get(bookTitle);
        List<Reservation> reservations = reservationService.getReservationsForBook(book.getId());
        assertFalse(reservations.isEmpty());
        assertEquals(member.getId(), reservations.get(reservations.size() - 1).getMemberId());
    }

    @Then("there are {int} reservations for {string}")
    public void thereAreReservationsFor(int count, String bookTitle) {
        Book book = books.get(bookTitle);
        List<Reservation> reservations = reservationService.getReservationsForBook(book.getId());
        assertEquals(count, reservations.size());
    }

    @Then("{string} is available again")
    public void bookIsAvailableAgain(String bookTitle) {
        Book book = books.get(bookTitle);
        assertTrue(bookRepository.findById(book.getId()).map(Book::isAvailable).orElse(false));
    }

    @Then("{string} is first in the reservation queue for {string}")
    public void memberIsFirstInQueue(String memberName, String bookTitle) {
        Member member = members.get(memberName);
        Book book = books.get(bookTitle);
        List<Reservation> reservations = reservationService.getReservationsForBook(book.getId());
        assertFalse(reservations.isEmpty());
        assertEquals(member.getId(), reservations.get(0).getMemberId());
    }

    @Then("the reservation is rejected because the member is suspended")
    public void reservationRejectedMemberSuspended() {
        assertNotNull(lastException);
        assertInstanceOf(MemberSuspendedException.class, lastException);
    }

    @Then("the penalty for {string} loan is {double} euros")
    public void penaltyForLoanIs(String memberName, double expectedPenalty) {
        assertNotNull(lastLoan);
        assertEquals(expectedPenalty, lastLoan.getPenalty(), 0.001);
    }
}
