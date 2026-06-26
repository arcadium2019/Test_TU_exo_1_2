package org.example.exo14.service;

import org.example.exo14.exception.MemberSuspendedException;
import org.example.exo14.model.Member;
import org.example.exo14.model.Reservation;
import org.example.exo14.repository.MemberRepository;
import org.example.exo14.repository.ReservationRepository;

import java.time.LocalDate;
import java.util.List;

public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final MemberRepository memberRepository;

    public ReservationService(ReservationRepository reservationRepository,
                              MemberRepository memberRepository) {
        this.reservationRepository = reservationRepository;
        this.memberRepository = memberRepository;
    }

    public Reservation createReservation(String memberId, String bookId, LocalDate date) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("Member not found: " + memberId));
        if (member.isSuspended()) {
            throw new MemberSuspendedException("Member " + memberId + " is suspended");
        }
        return reservationRepository.save(new Reservation(memberId, bookId, date));
    }

    public List<Reservation> getReservationsForBook(String bookId) {
        return reservationRepository.findByBookIdOrderedByDate(bookId);
    }
}
