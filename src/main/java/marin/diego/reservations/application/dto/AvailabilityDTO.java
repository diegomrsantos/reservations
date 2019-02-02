package marin.diego.reservations.application.dto;

import java.util.ArrayList;
import java.util.List;

public class AvailabilityDTO {

    private String startDate;
    private String endDate;

    private List<String> notAvailableDates;

    public AvailabilityDTO(String startDate, String endDate) {
        this.startDate = startDate;
        this.endDate = endDate;
        this.notAvailableDates = new ArrayList<>();
    }

    public String getStartDate() {
        return startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public List<String> getNotAvailableDates() {
        return notAvailableDates;
    }

    public void addNotAvailableDate(String date) {
        this.notAvailableDates.add(date);
    }
}
