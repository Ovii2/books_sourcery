package org.example.company.exception.handler;

import org.example.company.exception.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<ErrorDetails> exceptionUserAlreadyExistsHandler(UserAlreadyExistsException ex) {
        ErrorDetails errorDetails = new ErrorDetails(ex.getMessage());
        return new ResponseEntity<>(errorDetails, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ErrorDetails> exceptionUserNotFoundHandler(UserNotFoundException ex) {
        ErrorDetails errorDetails = new ErrorDetails(ex.getMessage());
        return new ResponseEntity<>(errorDetails, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(UsernameOrPasswordInvalidException.class)
    public ResponseEntity<ErrorDetails> exceptionUsernameOrPasswordInvalidHandler(UsernameOrPasswordInvalidException ex) {
        ErrorDetails errorDetails = new ErrorDetails(ex.getMessage());
        return new ResponseEntity<>(errorDetails, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(NotAdminException.class)
    public ResponseEntity<ErrorDetails> exceptionNotAdminHandler(NotAdminException ex) {
        ErrorDetails errorDetails = new ErrorDetails(ex.getMessage());
        return new ResponseEntity<>(errorDetails, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(BookNotFoundException.class)
    public ResponseEntity<ErrorDetails> exceptionBookNotFoundHandler(BookNotFoundException ex) {
        ErrorDetails errorDetails = new ErrorDetails(ex.getMessage());
        return new ResponseEntity<>(errorDetails, HttpStatus.NOT_FOUND);
    }
}
