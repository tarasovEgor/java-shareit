package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.Optional;

public interface UserService {

    UserDto saveUser(User user);

    UserDto getUserById(long id);

    List<UserDto> getAllUsers();

    UserDto updateUser(long id, User user);

    void deleteUser(long id);
}
