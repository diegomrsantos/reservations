package marin.diego.reservations.domain.repository;

import marin.diego.reservations.domain.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface ReservationRepository extends JpaRepository<Reservation, UUID> {

    List<Reservation> findAllByArrivalLessThanEqualAndDepartureGreaterThanEqualAndCanceledFalseOrderByArrival(
            LocalDateTime departure, LocalDateTime arrival);

    @Modifying
    @Query("update #{#entityName} r set r.canceled = :canceled where r.id = :id")
    int setCanceledForReservation(@Param("canceled") Boolean canceled, @Param("id") UUID id);
}
