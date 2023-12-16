package ru.practicum.shareit.booking.exception;

public class UnsupportedBookingStatusException extends RuntimeException {
    public UnsupportedBookingStatusException(String message) {
        super(message);
    }

}
