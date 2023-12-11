package ru.practicum.shareit.user.dao;

import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.Optional;

public interface UserDao {

    User saveUser(User user);

    Optional<User> getUser(long id);

    List<User> getAllUsers();

    User updateUser(long id, User user);

    void deleteUser(long id);

}
