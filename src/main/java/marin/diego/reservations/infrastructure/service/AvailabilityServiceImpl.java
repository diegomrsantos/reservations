package marin.diego.reservations.infrastructure.service;

import marin.diego.reservations.domain.repository.ReservationRepository;
import marin.diego.reservations.domain.Availability;
import marin.diego.reservations.domain.Reservation;
import marin.diego.reservations.domain.exception.ReservationException;
import marin.diego.reservations.domain.service.AvailabilityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Service
public class AvailabilityServiceImpl implements AvailabilityService {

    @Autowired
    private ReservationRepository reservationRepository;

    private Clock clock;

    public AvailabilityServiceImpl() {
        this.clock = Clock.systemUTC();
    }

    public AvailabilityServiceImpl(ReservationRepository reservationRepository, Clock clock) {
        this.reservationRepository = reservationRepository;
        this.clock = clock;
    }

    @Override
    public Availability getAvailability(String startDateQuery, String endDateQuery) {

        var startLocalDate = tryToParseDate(startDateQuery);
        var endLocalDate = tryToParseDate(endDateQuery);

        assertIsValidPeriod(startLocalDate, endLocalDate);

        var availability = new Availability(startLocalDate, endLocalDate);
        var reservations = reservationRepository.findAllByArrivalLessThanEqualAndDepartureGreaterThanEqualAndCanceledFalseOrderByArrival(
                LocalDateTime.of(endLocalDate, LocalTime.MAX), LocalDateTime.of(startLocalDate, LocalTime.MIN));

        for (Reservation reservation : reservations) {
            LocalDate start = reservation.getArrival().toLocalDate();
            LocalDate end = reservation.getDeparture().toLocalDate();
            while (start.isBefore(end) && !start.isAfter(endLocalDate)) {
                if (start.isBefore(startLocalDate)) {
                    start = start.plusDays(1);
                    continue;
                }
                availability.addNotAvailableDate(start);
                start = start.plusDays(1);
            }

        }
        return availability;
    }

    @Override
    public Availability getAvailability() {
        final LocalDate start = LocalDate.now(clock).plusDays(1);
        return getAvailability(start.toString(), start.plusMonths(1).toString());
    }

    private void assertIsValidPeriod(LocalDate startLocalDate, LocalDate endLocalDate) {
        if (startLocalDate.isAfter(endLocalDate)) {
            throw new ReservationException("Start date cannot be after End date.");
        }
        if (startLocalDate.isBefore(LocalDate.now(clock))) {
            throw new ReservationException("Start date cannot be in the past.");
        }
    }

    private LocalDate tryToParseDate(String date) {
        LocalDate startLocalDate;
        try {
            startLocalDate = LocalDate.parse(date);
        } catch (Exception e) {
            throw new ReservationException("It was not possible to parse '" + date
                    + "'. Please, provide a valid date in the format yyyy-MM-dd.");
        }
        return startLocalDate;
    }

}
