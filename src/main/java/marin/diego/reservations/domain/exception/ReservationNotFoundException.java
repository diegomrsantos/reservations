package marin.diego.reservations.domain.exception;

import java.util.UUID;

public class ReservationNotFoundException extends RuntimeException{

    public ReservationNotFoundException(UUID uuid) {
        super("Could not found reservation " + uuid);
    }
}
