package ru.practicum.shareit.exception;

public class UserDuplicateEmailException extends RuntimeException {
    public UserDuplicateEmailException(String message) {
        super(message);
    }
}
