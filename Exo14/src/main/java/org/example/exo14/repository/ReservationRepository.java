package org.example.exo14.repository;

import org.example.exo14.model.Reservation;

import java.util.List;
import java.util.Optional;

public interface ReservationRepository {
    Reservation save(Reservation reservation);
    Optional<Reservation> findById(String id);
    List<Reservation> findByBookIdOrderedByDate(String bookId);
}
