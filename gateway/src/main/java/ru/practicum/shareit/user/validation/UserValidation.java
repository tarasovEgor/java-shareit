package ru.practicum.shareit.user.validation;

import ru.practicum.shareit.user.exception.InvalidEmailException;
import ru.practicum.shareit.user.exception.UserDoesNotExistException;
import ru.practicum.shareit.user.model.User;

import java.util.Optional;

public class UserValidation {
    public static boolean isUserEmailValid(User user) {
        if (user.getEmail() == null) {
            throw new InvalidEmailException("Please, enter an email address.");
        }
        if (!user.getEmail().contains("@")) {
            throw new InvalidEmailException("Please, enter a valid email address.");
        }
        return true;
    }

    public static boolean optionalOfUserIsNotEmpty(Optional<User> user) {
        if (user.isEmpty()) {
            throw new UserDoesNotExistException("User doesn't exist.");
        } else {
            return true;
        }
    }
}
