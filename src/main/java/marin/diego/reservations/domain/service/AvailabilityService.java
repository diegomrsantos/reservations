package marin.diego.reservations.domain.service;

import marin.diego.reservations.domain.Availability;


public interface AvailabilityService {

    Availability getAvailability(String startDate, String endDate);

    Availability getAvailability();
}
