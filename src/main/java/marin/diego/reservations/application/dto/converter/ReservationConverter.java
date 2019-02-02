package marin.diego.reservations.application.dto.converter;

import marin.diego.reservations.application.dto.ReservationDTO;
import marin.diego.reservations.domain.Email;
import marin.diego.reservations.domain.Reservation;
import marin.diego.reservations.domain.User;

import java.util.UUID;

public class ReservationConverter {

    public Reservation toNewEntity(ReservationDTO reservationDTO) {
        final User user = new User(reservationDTO.getFirstName(),
                reservationDTO.getLastName(),
                new Email(reservationDTO.getEmail()));
        return  new Reservation(user,
                reservationDTO.getArrival(),
                reservationDTO.getDeparture());
    }

    public Reservation toExistingEntity(ReservationDTO reservationDTO) {
        final User user = new User(reservationDTO.getFirstName(),
                reservationDTO.getLastName(),
                new Email(reservationDTO.getEmail()));
        return  Reservation.of(UUID.fromString(reservationDTO.getId()),
                user,
                reservationDTO.getArrival(),
                reservationDTO.getDeparture(),
                Boolean.parseBoolean(reservationDTO.getCanceled()),
                reservationDTO.getVersion());
    }

    public ReservationDTO toDTO(Reservation reservation) {
        return ReservationDTO.of(reservation.getId().toString(),
                reservation.getUser().getFirstName(),
                reservation.getUser().getLastName(),
                reservation.getUser().getEmail().toString(),
                reservation.getArrival().toLocalDate().toString(),
                reservation.getDeparture().toLocalDate().toString(),
                reservation.isCanceled().toString(),
                reservation.getVersion().toString());
    }
}
