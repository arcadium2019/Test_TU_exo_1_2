package org.example.exo14.repository;

import org.example.exo14.model.Reservation;

import java.util.*;
import java.util.stream.Collectors;

public class InMemoryReservationRepository implements ReservationRepository {
    private final Map<String, Reservation> store = new LinkedHashMap<>();

    @Override
    public Reservation save(Reservation reservation) {
        store.put(reservation.getId(), reservation);
        return reservation;
    }

    @Override
    public Optional<Reservation> findById(String id) {
        return Optional.ofNullable(store.get(id));
    }

    @Override
    public List<Reservation> findByBookIdOrderedByDate(String bookId) {
        return store.values().stream()
                .filter(r -> r.getBookId().equals(bookId))
                .sorted(Comparator.comparing(Reservation::getReservationDate))
                .collect(Collectors.toList());
    }
}
