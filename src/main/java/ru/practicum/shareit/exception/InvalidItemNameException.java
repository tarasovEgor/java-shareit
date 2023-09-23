package ru.practicum.shareit.exception;

public class InvalidItemNameException extends RuntimeException {
    public InvalidItemNameException(String message) {
        super(message);
    }
}
