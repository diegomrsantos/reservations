package marin.diego.reservations.domain;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Availability {

    private LocalDate stardDate;
    private LocalDate endDate;

    private List<LocalDate> notAvailableDates;

    public Availability(LocalDate stardDate, LocalDate endDate) {
        this.stardDate = stardDate;
        this.endDate = endDate;
        this.notAvailableDates = new ArrayList<>();
    }

    public LocalDate getStardDate() {
        return stardDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public List<LocalDate> getNotAvailableDates() {
        return notAvailableDates;
    }

    public void addNotAvailableDate(LocalDate date) {
        this.notAvailableDates.add(date);
    }
}
