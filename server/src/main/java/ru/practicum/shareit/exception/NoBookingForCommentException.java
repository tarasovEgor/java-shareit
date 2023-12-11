package ru.practicum.shareit.exception;

public class NoBookingForCommentException extends RuntimeException {
    public NoBookingForCommentException(String message) {
        super(message);
    }
}
