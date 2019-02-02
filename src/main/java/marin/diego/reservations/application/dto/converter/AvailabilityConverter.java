package marin.diego.reservations.application.dto.converter;

import marin.diego.reservations.application.dto.AvailabilityDTO;
import marin.diego.reservations.domain.Availability;

import java.time.LocalDate;

public class AvailabilityConverter {

    public AvailabilityDTO toDTO(Availability availability) {
        final AvailabilityDTO availabilityDTO = new AvailabilityDTO(availability.getStardDate().toString(),
                availability.getEndDate().toString());

        for (LocalDate date : availability.getNotAvailableDates()) {
            availabilityDTO.addNotAvailableDate(date.toString());
        }
        return availabilityDTO;
    }
}
