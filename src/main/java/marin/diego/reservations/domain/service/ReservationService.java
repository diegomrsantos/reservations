package marin.diego.reservations.domain.service;

import marin.diego.reservations.domain.Reservation;

import java.util.List;
import java.util.UUID;

public interface ReservationService {

    Reservation save(Reservation reservation);

    Reservation update(Reservation reservation);

    Reservation findById(UUID uuid);

    List<Reservation> findAll();

    void cancel(UUID uuid);

    void deleteAll();
}
