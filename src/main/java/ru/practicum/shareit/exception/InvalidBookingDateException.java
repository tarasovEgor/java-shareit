package ru.practicum.shareit.exception;

public class InvalidBookingDateException extends RuntimeException {

    public InvalidBookingDateException(String message) {
        super(message);
    }
}
