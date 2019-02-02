package marin.diego.reservations.domain;

import marin.diego.reservations.domain.exception.ReservationException;

import javax.persistence.*;
import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import static java.time.temporal.ChronoUnit.DAYS;

@Entity
@Table(name = "reservation", indexes = {@Index(name = "idx_arrival_departure", columnList = "arrival, departure")})
public class Reservation implements Comparable<Reservation> {

    public static final String THE_CAMPSITE_CAN_BE_RESERVED_FOR_MAX_3_DAYS = "The campsite can be reserved for max 3 days";
    private static final LocalTime ARRIVAL_TIME = LocalTime.of(12, 1, 0);
    private static final LocalTime DEPARTURE_TIME = LocalTime.of(12, 0, 0);
    private static final int MAX_RESERVED_DAYS = 3;

    public static final String ARRIVAL_DATE_CANNOT_BE_AFTER_DEPARTURE_DATE = "Arrival date cannot be after departure date";
    public static final String MIN_MAX_DAY_AHEAD_OF_ARRIVAL = "The campsite must be reserved minimum 1 day(s) ahead of arrival and up to 1 month in advance";

    @Id
    @Column(name = "id", updatable = false)
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    private User user;

    @Column(name = "arrival")
    private LocalDateTime arrival;

    @Column(name = "departure")
    private LocalDateTime departure;

    @Column(name = "canceled", updatable = false)
    private Boolean canceled;

    @Column(name = "version")
    @Version
    private Long version;

    @Transient
    private Clock clock;

    public Reservation() {
    }

    public Reservation(User user, String arrival, String departure) {
        this(user, arrival, departure, false, Clock.systemUTC());
    }

    public Reservation(User user, String arrival, String departure, boolean canceled, Clock clock) {
        this.user = user;

        LocalDate arrivalLocalDate;
        try {
            arrivalLocalDate = LocalDate.parse(arrival);
            this.arrival = LocalDateTime.of(arrivalLocalDate, ARRIVAL_TIME);
        } catch (Exception e) {
            throw new ReservationException("It was not possible to parse '" + arrival
                    + "'. Please, provide a valid date in the format yyyy-MM-dd.");
        }

        LocalDate departureLocalDate;
        try {
            departureLocalDate = LocalDate.parse(departure);
            this.departure = LocalDateTime.of(departureLocalDate, DEPARTURE_TIME);
        } catch (Exception e) {
            throw new ReservationException("It was not possible to parse '" + departure
                    + "'. Please, provide a valid date in the format yyyy-MM-dd.");
        }
        this.canceled = canceled;
        this.clock = clock;
        final List<String> violations = checkViolations(arrivalLocalDate, departureLocalDate);
        if (!violations.isEmpty()) {
            throw new ReservationException(violations.toString());
        }
    }

    private Reservation(UUID id, User user, String arrival, String departure, boolean canceled, String version, Clock clock) {
        this(user, arrival, departure, canceled, clock);
        this.id = id;
        if (version != null && version.matches("^\\d+$")) {
            this.version = Long.parseLong(version);
        } else {
            throw new ReservationException("It was not possible to parse version provided '" + version + "'.");
        }
    }

    public static Reservation of(UUID id, User user, String arrival, String departure, boolean canceled, String version) {
        return new Reservation(id, user, arrival, departure, canceled, version, Clock.systemUTC());
    }

    public static Reservation of(UUID id, User user, String arrival, String departure, boolean canceled, String version, Clock clock) {
        return new Reservation(id, user, arrival, departure, canceled, version, clock);
    }

    public UUID getId() {
        return id;
    }

    public LocalDateTime getArrival() {
        return arrival;
    }

    public LocalDateTime getDeparture() {
        return departure;
    }

    public Boolean isCanceled() {
        return canceled;
    }

    public User getUser() {
        return user;
    }

    public Long getVersion() {
        return version;
    }

    private List<String> checkViolations(LocalDate arrival, LocalDate departure) {
        List<String> violations = new ArrayList<>();
        final LocalDate today = LocalDate.now(clock);

        if (departure.isBefore(arrival)) {
            violations.add(ARRIVAL_DATE_CANNOT_BE_AFTER_DEPARTURE_DATE);
        }
        if (DAYS.between(arrival, departure) > MAX_RESERVED_DAYS - 1){
            violations.add(THE_CAMPSITE_CAN_BE_RESERVED_FOR_MAX_3_DAYS);
        }
        if (!arrival.isAfter(today) || arrival.isAfter(today.plusMonths(1))) {
            violations.add(MIN_MAX_DAY_AHEAD_OF_ARRIVAL);
        }
        return violations;

    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Reservation that = (Reservation) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public int compareTo(Reservation o) {
        final int cmp = arrival.compareTo(o.arrival);
        return cmp != 0 ? cmp : departure.compareTo(o.departure);
    }
}
