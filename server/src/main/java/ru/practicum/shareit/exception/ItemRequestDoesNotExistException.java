package ru.practicum.shareit.exception;

public class ItemRequestDoesNotExistException extends RuntimeException {
    public ItemRequestDoesNotExistException(String message) {
        super(message);
    }
}
