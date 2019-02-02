package marin.diego.reservations.application.resource;

import marin.diego.reservations.domain.exception.ReservationNotFoundException;
import marin.diego.reservations.domain.exception.ReservationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@Order(Ordered.HIGHEST_PRECEDENCE)
@ControllerAdvice
public class RestExceptionHandler extends ResponseEntityExceptionHandler {

   private Logger logger = LoggerFactory.getLogger(RestExceptionHandler.class);

   @ExceptionHandler(ReservationNotFoundException.class)
   @ResponseBody
   @ResponseStatus(HttpStatus.NOT_FOUND)
   protected ApiError handleEntityNotFound(
           ReservationNotFoundException ex) {
       return new ApiError(ex.getMessage());
   }

    @ExceptionHandler(ReservationException.class)
    protected ResponseEntity<Object> handleBadRequest(
            ReservationException ex) {

        return ResponseEntity.badRequest().body(new ApiError(ex.getMessage()));
    }

    @ExceptionHandler(Exception.class)
    protected ResponseEntity<Object> handleInternalServerErro(
            Exception ex) {

        logger.error(ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ApiError("There was an unexpected error when creating this reservation"));
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    @ResponseBody
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    protected ApiError handleDataIntegrityViolation() {
       return new ApiError("We are sorry, but there is a reservation for this period.");
    }

    @ExceptionHandler(ObjectOptimisticLockingFailureException.class)
    @ResponseBody
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    protected ApiError bjectOptimisticLockingFailureException() {
        return new ApiError("We are sorry, but either version provided is incorrect or this reservation was updated by another use.");
    }

}