package ru.practicum.shareit.exception;

public class BookingStatusIsAlreadyApprovedException extends RuntimeException {
    public BookingStatusIsAlreadyApprovedException(String message) {
        super(message);
    }
}
