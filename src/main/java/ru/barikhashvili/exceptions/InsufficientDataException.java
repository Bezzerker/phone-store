package ru.barikhashvili.exceptions;

public class InsufficientDataException extends RuntimeException {
    public InsufficientDataException() {
        super();
    }

    public InsufficientDataException(String message) {
        super(message);
    }
}
