package ru.practicum.shareit.exception;

public class UnsupportedBookingStatusException extends RuntimeException {
    public UnsupportedBookingStatusException(String message) {
        super(message);
    }
}
