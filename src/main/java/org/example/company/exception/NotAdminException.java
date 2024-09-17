package org.example.company.exception;

public class NotAdminException extends RuntimeException {

    public NotAdminException(String message) {
        super(message);
    }
}
