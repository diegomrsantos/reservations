package marin.diego.reservations.infrastructure.services;

import marin.diego.reservations.domain.Availability;
import marin.diego.reservations.domain.Email;
import marin.diego.reservations.domain.Reservation;
import marin.diego.reservations.domain.User;
import marin.diego.reservations.domain.repository.ReservationRepository;
import marin.diego.reservations.infrastructure.service.AvailabilityServiceImpl;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import java.time.*;
import java.util.Collections;
import java.util.List;

import static java.util.stream.Collectors.toList;
import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class AvailabilityServiceTests {

    private static AvailabilityServiceImpl availabilityService;
    private ReservationRepository reservationRepository;

    private static Clock fixed;
    private static User user;

    private final static String _2019_01_01 = "2019-01-01";
    private final static String _2019_01_02 = "2019-01-02";
    private final static String _2019_01_03 = "2019-01-03";
    private final static String _2019_01_04 = "2019-01-04";
    private final static String _2019_01_05 = "2019-01-05";
    private final static String _2019_01_06 = "2019-01-06";
    private final static String _2019_01_07 = "2019-01-07";
    private final static String _2019_01_08 = "2019-01-08";
    private final static String _2019_01_09 = "2019-01-09";
    private final static String _2019_01_10 = "2019-01-10";
    private final static String _2019_01_11 = "2019-01-11";
    private final static String _2019_01_12 = "2019-01-12";
    private final static String _2019_01_13 = "2019-01-13";
    private final static String _2019_01_14 = "2019-01-14";
    private final static String _2019_01_31 = "2019-01-31";

    private final static String _2019_02_02 = "2019-02-02";
    private final static String _2019_02_04 = "2019-02-04";


    @BeforeClass
    public static void setUp() {

        final Instant now = Instant.parse("2019-01-01T00:00:00.000Z");
        fixed = Clock.fixed(now, ZoneOffset.UTC);
        user = new User("firstName", "lastname", new Email("firstname@domain.com"));

    }

    @Before
    public void beforeEach() {
        reservationRepository = Mockito.mock(ReservationRepository.class);
        availabilityService = new AvailabilityServiceImpl(reservationRepository, fixed);
    }

    @Test
    public void returnAllDaysAreAvailableWhenThereAreNoReservations() {

        final LocalDate startDate = LocalDate.parse(_2019_01_01);
        final LocalDate endDate = LocalDate.parse(_2019_01_31);

        when(reservationRepository.findAllByArrivalLessThanEqualAndDepartureGreaterThanEqualAndCanceledFalseOrderByArrival(
                LocalDateTime.of(endDate, LocalTime.MAX), LocalDateTime.of(startDate, LocalTime.MIN)))
                .thenReturn(Collections.emptyList());

        final Availability availability = availabilityService.getAvailability(_2019_01_01, _2019_01_31);

        assertEquals(startDate, availability.getStardDate());
        assertEquals(endDate, availability.getEndDate());
        assertTrue(availability.getNotAvailableDates().isEmpty());
    }

    @Test
    public void startDateIsOneDayAfterTodayAndEndDateOneMonthLaterWhenSearchPeriodIsNotProvided() {

        final LocalDate startDate = LocalDate.parse(_2019_01_02);
        final LocalDate endDate = LocalDate.parse(_2019_02_02);

        when(reservationRepository.findAllByArrivalLessThanEqualAndDepartureGreaterThanEqualAndCanceledFalseOrderByArrival(
                LocalDateTime.of(endDate, LocalTime.MAX), LocalDateTime.of(startDate, LocalTime.MIN)))
                .thenReturn(Collections.emptyList());

        final Availability availability = availabilityService.getAvailability();

        assertEquals(startDate, availability.getStardDate());
        assertEquals(endDate, availability.getEndDate());
        assertTrue(availability.getNotAvailableDates().isEmpty());
    }

    @Test
    public void returnNotAvailableDaysWhenAllReservationsStartAndFinishWithinSearchPeriod() {
        final LocalDate startDate = LocalDate.parse(_2019_01_05);
        final LocalDate endDate = LocalDate.parse(_2019_02_04);

        var r1 = new Reservation(user, _2019_01_07, _2019_01_09, false, fixed);
        var r2 = new Reservation(user, _2019_01_10, _2019_01_12,false, fixed);
        var r3 = new Reservation(user, _2019_01_12, _2019_01_13, false, fixed);

        when(reservationRepository.findAllByArrivalLessThanEqualAndDepartureGreaterThanEqualAndCanceledFalseOrderByArrival(
                LocalDateTime.of(endDate, LocalTime.MAX), LocalDateTime.of(startDate, LocalTime.MIN)))
                .thenReturn(List.of(r1, r2, r3));

        final Availability availability = availabilityService.getAvailability(_2019_01_05, _2019_02_04);

        assertEquals(startDate, availability.getStardDate());
        assertEquals(endDate, availability.getEndDate());
        assertEquals(List.of(_2019_01_07, _2019_01_08, _2019_01_10, _2019_01_11, _2019_01_12)
                .stream().map((LocalDate::parse)).collect(toList()), availability.getNotAvailableDates());
    }

    @Test
    public void returnNotAvailableDaysWhenReservationsStartBeforeAndFinishWithinSearchPeriod() {
        final LocalDate startDate = LocalDate.parse(_2019_01_05);
        final LocalDate endDate = LocalDate.parse(_2019_01_13);

        var r1 = new Reservation(user, _2019_01_04, _2019_01_06, false, fixed);
        var r2 = new Reservation(user, _2019_01_10, _2019_01_12,false, fixed);

        when(reservationRepository.findAllByArrivalLessThanEqualAndDepartureGreaterThanEqualAndCanceledFalseOrderByArrival(
                LocalDateTime.of(endDate, LocalTime.MAX), LocalDateTime.of(startDate, LocalTime.MIN)))
                .thenReturn(List.of(r1, r2));

        final Availability availability = availabilityService.getAvailability(_2019_01_05, _2019_01_13);

        assertEquals(startDate, availability.getStardDate());
        assertEquals(endDate, availability.getEndDate());
        assertEquals(List.of(_2019_01_05, _2019_01_10, _2019_01_11).stream().map((LocalDate::parse)).collect(toList())
                , availability.getNotAvailableDates());
    }

    @Test
    public void returnNotAvailableDaysWhenReservationsStartWithinAndFinishAfterSearchPeriod() {
        final LocalDate startDate = LocalDate.parse(_2019_01_05);
        final LocalDate endDate = LocalDate.parse(_2019_01_13);

        var r1 = new Reservation(user, _2019_01_06, _2019_01_07, false, fixed);
        var r2 = new Reservation(user, _2019_01_12, _2019_01_14,false, fixed);

        when(reservationRepository.findAllByArrivalLessThanEqualAndDepartureGreaterThanEqualAndCanceledFalseOrderByArrival(
                LocalDateTime.of(endDate, LocalTime.MAX), LocalDateTime.of(startDate, LocalTime.MIN)))
                .thenReturn(List.of(r1, r2));

        final Availability availability = availabilityService.getAvailability(_2019_01_05, _2019_01_13);

        assertEquals(startDate, availability.getStardDate());
        assertEquals(endDate, availability.getEndDate());
        assertEquals(List.of(_2019_01_06, _2019_01_12, _2019_01_13).stream().map((LocalDate::parse)).collect(toList()),
                availability.getNotAvailableDates());
    }

    @Test
    public void returnNotAvailableDaysWhenReservationsStartBeforeAndFinishAfterSearchPeriod() {
        final LocalDate startDate = LocalDate.parse(_2019_01_05);
        final LocalDate endDate = LocalDate.parse(_2019_01_06);

        var r1 = new Reservation(user, _2019_01_04, _2019_01_06, false, fixed);
        var r2 = new Reservation(user, _2019_01_06, _2019_01_07,false, fixed);

        when(reservationRepository.findAllByArrivalLessThanEqualAndDepartureGreaterThanEqualAndCanceledFalseOrderByArrival(
                LocalDateTime.of(endDate, LocalTime.MAX), LocalDateTime.of(startDate, LocalTime.MIN)))
                .thenReturn(List.of(r1, r2));

        final Availability availability = availabilityService.getAvailability(_2019_01_05, _2019_01_06);

        assertEquals(startDate, availability.getStardDate());
        assertEquals(endDate, availability.getEndDate());
        assertEquals(List.of(_2019_01_05, _2019_01_06).stream().map((LocalDate::parse)).collect(toList()),
                availability.getNotAvailableDates());
    }
}
