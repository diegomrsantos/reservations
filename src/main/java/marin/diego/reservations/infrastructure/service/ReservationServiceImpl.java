package marin.diego.reservations.infrastructure.service;

import marin.diego.reservations.domain.exception.ReservationNotFoundException;
import marin.diego.reservations.domain.repository.ReservationRepository;
import marin.diego.reservations.domain.service.ReservationService;
import marin.diego.reservations.domain.Reservation;
import marin.diego.reservations.domain.exception.ReservationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.UUID;

@Service
public class ReservationServiceImpl implements ReservationService {

    @Autowired
    private ReservationRepository reservationRepository;

    @Transactional
    public Reservation save(Reservation reservation) {
        return reservationRepository.save(reservation);
    }

    @Transactional
    public Reservation update(Reservation reservation) {
        var persistedReservation = findByIdOrThrowException(reservation.getId());
        if (persistedReservation.isCanceled()) {
            throw new ReservationException("This reservations has been canceled");
        }
        return reservationRepository.save(reservation);
    }

    public Reservation findById(UUID uuid) {
        return reservationRepository.findById(uuid).orElseThrow(
                () -> new ReservationNotFoundException(uuid));
    }

    public List<Reservation> findAll() {
        return reservationRepository.findAll();
    }

    @Transactional
    public void cancel(UUID uuid) {
        var reservation = findByIdOrThrowException(uuid);
        if (reservation.isCanceled()) {
            throw new ReservationException("This reservations has already been canceled");
        }
        reservationRepository.setCanceledForReservation(true, uuid);
    }

    @Override
    public void deleteAll() {
        reservationRepository.deleteAll();
    }

    private Reservation findByIdOrThrowException(UUID id) {
        return reservationRepository
                .findById(id).orElseThrow(
                        () -> new ReservationNotFoundException(id)
                );
    }
}
