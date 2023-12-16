package ru.practicum.shareit.validation;

import ru.practicum.shareit.exception.UserDoesNotExistException;
import ru.practicum.shareit.user.model.User;

import java.util.Optional;

public class UserValidation {

    public static boolean optionalOfUserIsNotEmpty(Optional<User> user) {
        if (user.isEmpty()) {
            throw new UserDoesNotExistException("User doesn't exist.");
        } else {
            return true;
        }
    }

}
