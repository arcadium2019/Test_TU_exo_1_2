package org.example.exo14.model;

import java.time.LocalDate;
import java.util.UUID;

public class Loan {
    private final String id;
    private final String memberId;
    private final String bookId;
    private final LocalDate loanDate;
    private final LocalDate dueDate;
    private LocalDate returnDate;
    private double penalty;

    public Loan(String memberId, String bookId, LocalDate loanDate) {
        this.id = UUID.randomUUID().toString();
        this.memberId = memberId;
        this.bookId = bookId;
        this.loanDate = loanDate;
        this.dueDate = loanDate.plusDays(21);
        this.penalty = 0.0;
    }

    public String getId()          { return id; }
    public String getMemberId()    { return memberId; }
    public String getBookId()      { return bookId; }
    public LocalDate getLoanDate() { return loanDate; }
    public LocalDate getDueDate()  { return dueDate; }
    public LocalDate getReturnDate() { return returnDate; }
    public void setReturnDate(LocalDate returnDate) { this.returnDate = returnDate; }
    public double getPenalty()     { return penalty; }
    public void setPenalty(double penalty) { this.penalty = penalty; }
}
