package ru.practicum.shareit.exception;

public class InvalidItemRequestDescriptionException extends RuntimeException {
    public InvalidItemRequestDescriptionException(String message) {
        super(message);
    }
}
