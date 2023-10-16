package ru.practicum.shareit.validation;

import ru.practicum.shareit.exception.InvalidEmailException;
import ru.practicum.shareit.exception.UserDoesNotExistException;
import ru.practicum.shareit.exception.UserDuplicateEmailException;
import ru.practicum.shareit.user.model.User;

import java.util.Map;
import java.util.Objects;
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

    public static boolean isUserEmailValid(User user, Map<Long, User> users) {
        for (User u : users.values()) {
            if (Objects.equals(user.getEmail(), u.getEmail())) {
                throw new UserDuplicateEmailException("User with the email: " + user.getEmail() + " already exists.");
            }
            if (user.getEmail() == null) {
                throw new InvalidEmailException("Please, enter an email address.");
            }
            if (!user.getEmail().contains("@")) {
                throw new InvalidEmailException("Please, enter a valid email address.");
            }
        }
        return true;
    }

    public static Optional<User> isUserValidForUpdate(User user, Optional<User> optUser, Map<Long, User> users) {
        if (optUser.isEmpty()) {
            throw new UserDoesNotExistException("User does not exist.");
        }
        if (user.getName() != null && user.getEmail() != null) {
            optUser.get().setName(user.getName());
            optUser.get().setEmail(user.getEmail());
        } else if (user.getName() != null) {
            optUser.get().setName(user.getName());
        } else if (user.getEmail() != null) {
            if (optUser.get().getEmail().equals(user.getEmail())) {
                optUser.get().setEmail(user.getEmail());
            } else {
                for (User u : users.values()) {
                    if (Objects.equals(u.getEmail(), user.getEmail())) {
                        throw new UserDuplicateEmailException("User with this email already exists.");
                    }
                }
                optUser.get().setEmail(user.getEmail());
            }
        }
        users.put(optUser.get().getId(), optUser.get());
        return optUser;
    }

    public static boolean optionalOfUserIsNotEmpty(Optional<User> user) {
        if (user.isEmpty()) {
            throw new UserDoesNotExistException("User doesn't exist.");
        } else {
            return true;
        }
    }

    public static User isUserValidForUpdate(User user, User updatedUser) {
        if (user == null) {
            throw new UserDoesNotExistException("User does not exist.");
        }
        if (user.getName() != null && user.getEmail() != null) {
            updatedUser.setName(user.getName());
            updatedUser.setEmail(user.getEmail());
        } else if (user.getName() != null) {
            updatedUser.setName(user.getName());
        } else if (user.getEmail() != null) {
            updatedUser.setEmail(user.getEmail());
        }
        return updatedUser;
    }


}
