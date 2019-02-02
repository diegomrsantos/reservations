package marin.diego.reservations.domain;

import marin.diego.reservations.domain.exception.ReservationException;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.time.*;
import java.util.List;
import java.util.UUID;

import static org.junit.Assert.assertEquals;

public class ReservationTest {

    private final static String _2019_01_01 = "2019-01-01";
    private final static String _2019_01_02 = "2019-01-02";
    private final static String _2019_01_03 = "2019-01-03";
    private final static String _2019_01_04 = "2019-01-04";
    private final static String _2019_01_05 = "2019-01-05";

    private final static String _2019_02_02 = "2019-02-02";
    private final static String _2019_02_03 = "2019-02-03";

    private static final String FIRST_NAME = "firstName";
    private static final String LAST_NAME = "lastname";
    private static final String EMAIL = "firstname@domain.com";
    private static Clock fixed;
    private static User user;

    @BeforeClass
    public static void setUp() {
        final Instant now = Instant.parse("2019-01-01T00:00:00.000Z");
        fixed = Clock.fixed(now, ZoneOffset.UTC);
        user = new User(FIRST_NAME, LAST_NAME, new Email(EMAIL));

    }

    @Rule
    public ExpectedException exceptionRule = ExpectedException.none();

    @Test
    public void reservationIsCreatedWhenThereIsNoConstraintViolationTest() {

        final LocalTime arrivalTime = LocalTime.of(12, 1, 0);
        final LocalTime departureTime = LocalTime.of(12, 0, 0);

        var reservation = new Reservation(user, _2019_01_02, _2019_01_04, false, fixed);

        assertEquals(LocalDateTime.of(LocalDate.parse(_2019_01_02), arrivalTime), reservation.getArrival());
        assertEquals(LocalDateTime.of(LocalDate.parse(_2019_01_04), departureTime), reservation.getDeparture());
        assertEquals(user, reservation.getUser());
        assertEquals(false, reservation.isCanceled());
    }

    @Test
    public void exceptionIsThrownWhenDepartureIsBeforeArrivalCreationTest() {

        exceptionRule.expect(ReservationException.class);
        exceptionRule.expectMessage(List.of(Reservation.ARRIVAL_DATE_CANNOT_BE_AFTER_DEPARTURE_DATE).toString());

        new Reservation(user, _2019_01_04, _2019_01_02, false, fixed);
    }

    @Test
    public void exceptionIsThrownWhenReservationIsForMoreThanThreeDaysTest() {

        exceptionRule.expect(ReservationException.class);
        exceptionRule.expectMessage(List.of(Reservation.THE_CAMPSITE_CAN_BE_RESERVED_FOR_MAX_3_DAYS).toString());

        new Reservation(user, _2019_01_02, _2019_01_05, false, fixed);
    }

    @Test
    public void exceptionIsThrownWhenReservationIsNotForMinimumOneDayAheadOfArrival() {

        var uuid = UUID.randomUUID();

        exceptionRule.expect(ReservationException.class);
        exceptionRule.expectMessage(List.of(Reservation.MIN_MAX_DAY_AHEAD_OF_ARRIVAL).toString());

        new Reservation(user, _2019_01_01, _2019_01_03, false, fixed);
    }

    @Test
    public void exceptionIsThrownWhenReservationIsNotForMaximumOneMonthAheadOfArrival() {

        var uuid = UUID.randomUUID();

        exceptionRule.expect(ReservationException.class);
        exceptionRule.expectMessage(List.of(Reservation.MIN_MAX_DAY_AHEAD_OF_ARRIVAL).toString());

        new Reservation(user, _2019_02_02, _2019_02_03, false, fixed);
    }
}
