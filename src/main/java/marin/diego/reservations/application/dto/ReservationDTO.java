package marin.diego.reservations.application.dto;

public class ReservationDTO {
    private String id;
    private String firstName;
    private String lastName;
    private String email;
    private String arrival;
    private String departure;
    private String canceled;
    private String version;

    public ReservationDTO() {

    }

    public ReservationDTO(String firstName, String lastName, String email, String arrival, String departure) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.arrival = arrival;
        this.departure = departure;
    }

    private ReservationDTO(String id, String firstName, String lastName, String email, String arrival, String departure,
                           String canceled, String version) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.arrival = arrival;
        this.departure = departure;
        this.canceled = canceled;
        this.version = version;
    }

    public static ReservationDTO of(String id, String firstName, String lastName, String email, String arrival,
                                    String departure, String isCanceled, String version) {
        return new ReservationDTO(id, firstName, lastName, email, arrival, departure, isCanceled, version);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getArrival() {
        return arrival;
    }

    public void setArrival(String arrival) {
        this.arrival = arrival;
    }

    public String getDeparture() {
        return departure;
    }

    public void setDeparture(String departure) {
        this.departure = departure;
    }

    public String getCanceled() {
        return canceled;
    }

    public void setCanceled(String canceled) {
        this.canceled = canceled;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }
}
