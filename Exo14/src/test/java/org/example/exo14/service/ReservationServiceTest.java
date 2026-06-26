package org.example.exo14.service;

import org.example.exo14.exception.MemberSuspendedException;
import org.example.exo14.model.Member;
import org.example.exo14.model.Reservation;
import org.example.exo14.repository.MemberRepository;
import org.example.exo14.repository.ReservationRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReservationServiceTest {

    @Mock private ReservationRepository reservationRepository;
    @Mock private MemberRepository memberRepository;

    @InjectMocks
    private ReservationService service;

    private static final LocalDate TODAY = LocalDate.of(2026, 6, 1);

    @Test
    void create_reservation_for_active_member_succeeds() {
        // Arrange
        Member member = new Member("M1", "Alice");
        when(memberRepository.findById("M1")).thenReturn(Optional.of(member));
        when(reservationRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        // Act
        Reservation result = service.createReservation("M1", "B1", TODAY);

        // Assert
        assertThat(result.getMemberId()).isEqualTo("M1");
        assertThat(result.getBookId()).isEqualTo("B1");
    }

    @Test
    void create_reservation_for_suspended_member_throws() {
        // Arrange
        Member member = new Member("M2", "Bob");
        member.setSuspended(true);
        when(memberRepository.findById("M2")).thenReturn(Optional.of(member));

        // Act & Assert
        assertThatThrownBy(() -> service.createReservation("M2", "B1", TODAY))
                .isInstanceOf(MemberSuspendedException.class);
    }

    @Test
    void get_reservations_returns_list_ordered_by_date() {
        // Arrange
        Reservation r1 = new Reservation("M1", "B1", TODAY);
        Reservation r2 = new Reservation("M2", "B1", TODAY.plusDays(1));
        when(reservationRepository.findByBookIdOrderedByDate("B1")).thenReturn(List.of(r1, r2));

        // Act
        List<Reservation> result = service.getReservationsForBook("B1");

        // Assert
        assertThat(result).hasSize(2);
        assertThat(result.get(0).getMemberId()).isEqualTo("M1");
    }
}
