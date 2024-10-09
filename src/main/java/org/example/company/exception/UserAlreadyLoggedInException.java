package org.example.company.exception;

public class UserAlreadyLoggedInException extends RuntimeException {

    public UserAlreadyLoggedInException(String message) {
        super(message);
    }
}
