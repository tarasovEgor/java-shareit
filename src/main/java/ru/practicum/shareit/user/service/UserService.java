package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.Optional;

public interface UserService {

    User saveUser(User user);

    Optional<User> getUserById(long id);

    List<User> getAllUsers();

    User updateUser(long id, User user);

    void deleteUser(long id);
}
