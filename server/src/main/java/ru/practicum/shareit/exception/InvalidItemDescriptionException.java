package ru.practicum.shareit.exception;

public class InvalidItemDescriptionException extends RuntimeException {
    public InvalidItemDescriptionException(String message) {
        super(message);
    }
}
