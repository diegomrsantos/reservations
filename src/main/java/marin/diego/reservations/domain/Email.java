package marin.diego.reservations.domain;

import javax.persistence.Embeddable;

@Embeddable
public class Email {

    private String email;

    public Email() {
    }

    public Email(String email) {
        if (!isValid(email)) {
            throw new IllegalArgumentException("Email provided is not valid.");
        }
        this.email = email;
    }

    public String getEmail() {
        return email;
    }

    @Override
    public String toString() {
        return email;
    }

    private boolean isValid(String value) {
        return true;
    }
}
