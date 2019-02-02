package marin.diego.reservations.application.resource;

public class ApiError {

    private String message;

    public ApiError(String message) {

        this.message = message;
    }

    public ApiError(String message, String debugMessage) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

}
