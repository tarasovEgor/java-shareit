package ru.practicum.shareit.exception;

public class OwnerDoesNotExistException extends RuntimeException {
    public OwnerDoesNotExistException(String message) {
        super(message);
    }
}
