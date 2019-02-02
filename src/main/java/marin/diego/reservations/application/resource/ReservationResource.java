package marin.diego.reservations.application.resource;

import marin.diego.reservations.application.dto.AvailabilityDTO;
import marin.diego.reservations.application.dto.ReservationDTO;
import marin.diego.reservations.application.dto.converter.AvailabilityConverter;
import marin.diego.reservations.application.dto.converter.ReservationConverter;
import marin.diego.reservations.domain.Availability;
import marin.diego.reservations.domain.service.ReservationService;
import marin.diego.reservations.domain.Reservation;
import marin.diego.reservations.domain.service.AvailabilityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.Optional;
import java.util.UUID;


@RestController
@RequestMapping("/v1.0/campsite")
public class ReservationResource {

    @Autowired
    private ReservationService reservationService;

    @Autowired
    private AvailabilityService availabilityService;

    @PostMapping(value = "/reservations", produces = {MediaType.APPLICATION_JSON_UTF8_VALUE})
    public ResponseEntity<ReservationDTO> create(@RequestBody ReservationDTO reservationDTO, UriComponentsBuilder ucb) {

        final ReservationConverter reservationConverter = new ReservationConverter();
        final Reservation reservation;
        reservation = reservationService.save(reservationConverter.toNewEntity(reservationDTO));
        final URI uri = ucb.path("/v1.0/campsite/reservations/{id}").buildAndExpand(reservation.getId()).toUri();
        return ResponseEntity.created(uri).body(reservationConverter.toDTO(reservation));
    }

    @GetMapping(value = "/reservations/{id}", produces = {MediaType.APPLICATION_JSON_UTF8_VALUE})
    public ResponseEntity<ReservationDTO> get(@PathVariable String id) {
        final ReservationConverter reservationConverter = new ReservationConverter();
        final Reservation reservation = reservationService.findById(UUID.fromString(id));
        return ResponseEntity.ok(reservationConverter.toDTO(reservation));
    }

    @PutMapping(value = "/reservations/{id}")
    public ResponseEntity<ReservationDTO> update(@PathVariable String id, @RequestBody ReservationDTO reservationDTO) {
        final ReservationConverter reservationConverter = new ReservationConverter();
        Reservation reservation;
        reservationDTO.setId(id);
        reservation = reservationService.update(reservationConverter.toExistingEntity(reservationDTO));
        return ResponseEntity.ok(reservationConverter.toDTO(reservation));

    }

    @DeleteMapping(value = "/reservations/{id}")
    public ResponseEntity<?> cancel(@PathVariable String id) {
        final UUID uuid = UUID.fromString(id);
        reservationService.cancel(uuid);
        return ResponseEntity.ok().build();
    }

    @GetMapping(value = {"/availability/{startDate}/{endDate}", "/availability"}, produces = {MediaType.APPLICATION_JSON_UTF8_VALUE})
    public ResponseEntity<AvailabilityDTO> getCampsiteAvailability(@PathVariable(name = "startDate", required = false) Optional<String> startDate,
                                                                   @PathVariable(name = "endDate", required = false) Optional<String> endDate) {

        Availability availability;
        if (startDate.isPresent() && endDate.isPresent()) {
            availability = availabilityService.getAvailability(startDate.get(), endDate.get());
        } else {
            availability = availabilityService.getAvailability();
        }

        final AvailabilityConverter availabilityConverter = new AvailabilityConverter();
        final AvailabilityDTO availabilityDTO = availabilityConverter.toDTO(availability);

        return ResponseEntity.ok(availabilityDTO);
    }
}
