package marin.diego.reservations.infrastructure.services;

import io.zonky.test.db.AutoConfigureEmbeddedDatabase;
import marin.diego.reservations.domain.Email;
import marin.diego.reservations.domain.Reservation;
import marin.diego.reservations.domain.User;
import marin.diego.reservations.domain.service.ReservationService;
import org.flywaydb.test.annotation.FlywayTest;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.lessThan;
import static org.junit.Assert.assertThat;

@RunWith(SpringRunner.class)
@AutoConfigureEmbeddedDatabase
@FlywayTest
@SpringBootTest
public class ReservationServiceIntegrationTest {

    @Autowired
    private ReservationService reservationService;

    private static Clock fixed;
    private static User user;

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

    @BeforeClass
    public static void setUp() {
        final Instant now = Instant.parse("2019-01-01T00:00:00.000Z");
        fixed = Clock.fixed(now, ZoneOffset.UTC);
        user = new User("firstName", "lastname", new Email("firstname@domain.com"));

    }

    @Before
    public void beforeEachTest() {
        reservationService.deleteAll();
    }


    @Test
    public void allReservationsArePersistedWhenConcurrentReservationsCreationTest() throws InterruptedException {

        final Thread thread1 = new Thread(
                () -> saveReservations(createReservations(2019, 1, 4, 18, 2))
        );

        final Thread thread2 = new Thread(
                () -> saveReservations(createReservations(2019, 1, 18, 28, 2))
        );

        thread1.start();
        thread2.start();
        thread1.join();
        thread2.join();

        var reservations = reservationService.findAll();
        assertThat(reservations.size(), equalTo(13));
        assertNoOverlapping(reservations);
    }

    @Test
    public void thereAreNoDoubleBookingWhenConcurrentOverlappingReservationsCreationTest() throws InterruptedException {

        final Thread thread1 = new Thread(
                () -> saveReservations(createReservations(2019, 1, 4, 18, 2))
        );

        final Thread thread2 = new Thread(
                () -> saveReservations(createReservations(2019, 1, 3, 19, 2))
        );

        thread1.start();
        thread2.start();
        thread1.join();
        thread2.join();

        var reservations = reservationService.findAll();
        assertNoOverlapping(reservations);
    }

    @Test
    public void thereAreNoDoubleBookingWhenConcurrentOverlappingReservationsUpdateTest() throws InterruptedException {

        var r1 = reservationService.save(new Reservation(user, _2019_01_02, _2019_01_04, false, fixed));
        var r2 = reservationService.save(new Reservation(user, _2019_01_04, _2019_01_06, false, fixed));
        var r3 = reservationService.save(new Reservation(user, _2019_01_06, _2019_01_08, false, fixed));
        var r4 = reservationService.save(new Reservation(user, _2019_01_08, _2019_01_10, false, fixed));

        final Thread thread1 = new Thread(() ->
           saveReservations(List.of(
                Reservation.of(r1.getId(), user, _2019_01_03, _2019_01_05, false, String.valueOf(r1.getVersion()), fixed),
                Reservation.of(r2.getId(), user, _2019_01_05, _2019_01_07, false, String.valueOf(r2.getVersion()), fixed),
                Reservation.of(r3.getId(), user, _2019_01_07, _2019_01_09, false, String.valueOf(r3.getVersion()), fixed),
                Reservation.of(r4.getId(), user, _2019_01_09, _2019_01_11, false, String.valueOf(r4.getVersion()), fixed)
                )
           )
        );

        final Thread thread2 = new Thread(() ->
            saveReservations(List.of(
                Reservation.of(r1.getId(), user, _2019_01_02, _2019_01_03, false, String.valueOf(r1.getVersion()), fixed),
                Reservation.of(r2.getId(), user, _2019_01_03, _2019_01_04, false, String.valueOf(r2.getVersion()), fixed),
                Reservation.of(r3.getId(), user, _2019_01_04, _2019_01_05, false, String.valueOf(r3.getVersion()), fixed),
                Reservation.of(r4.getId(), user, _2019_01_05, _2019_01_06, false, String.valueOf(r4.getVersion()), fixed)
                )
            )
        );

        thread1.start();
        thread2.start();
        thread1.join();
        thread2.join();

        var reservations = reservationService.findAll();
        assertNoOverlapping(reservations);
    }

    private List<Reservation> createReservations(int year, int month, int fromDay, int toDay, int durationInDays) {
        List<Reservation> reservations = new ArrayList<>();
        for (int i = fromDay; i <= toDay; i += durationInDays) {
            reservations.add(new Reservation(
                    user,
                    String.format("%s-%02d-%02d", year, month, i),
                    String.format("%s-%02d-%02d", year, month, i + durationInDays),
                    false,
                    fixed));
        }
        return reservations;
    }

    private void assertNoOverlapping(List<Reservation> reservations) {
        Collections.sort(reservations);

        for (int i = 0; i < reservations.size() - 1; i++) {
            final Reservation reservationBefore = reservations.get(i);
            final Reservation reservationAfter = reservations.get(i + 1);
            assertThat(reservationBefore.getDeparture(), lessThan(reservationAfter.getArrival()));
        }
    }

    private void saveReservations(List<Reservation> reservations) {
        for (Reservation reservation : reservations) {
            try {
                reservationService.save(reservation);
            } catch (Exception ignored) { }
        }
    }
}
