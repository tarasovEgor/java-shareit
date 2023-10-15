package ru.practicum.shareit.exception;

public class InvalidItemOwnerException extends RuntimeException {
    public InvalidItemOwnerException(String message) {
        super(message);
    }
}
