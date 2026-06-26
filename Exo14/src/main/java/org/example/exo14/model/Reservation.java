package org.example.exo14.model;

import java.time.LocalDate;
import java.util.UUID;

public class Reservation {
    private final String id;
    private final String memberId;
    private final String bookId;
    private final LocalDate reservationDate;

    public Reservation(String memberId, String bookId, LocalDate reservationDate) {
        this.id = UUID.randomUUID().toString();
        this.memberId = memberId;
        this.bookId = bookId;
        this.reservationDate = reservationDate;
    }

    public String getId()                    { return id; }
    public String getMemberId()              { return memberId; }
    public String getBookId()               { return bookId; }
    public LocalDate getReservationDate()   { return reservationDate; }
}
