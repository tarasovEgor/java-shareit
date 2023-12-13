package ru.practicum.shareit.user.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.impl.UserServiceImpl;

import java.util.List;

@RestController
@RequestMapping(path = "/users")
public class UserController {

    UserServiceImpl userServiceImpl;

    @Autowired
    public UserController(UserServiceImpl userServiceImpl) {
        this.userServiceImpl = userServiceImpl;
    }

    @PostMapping
    public UserDto saveUser(@RequestBody User user) {
        return userServiceImpl.saveUser(user);
    }

    @GetMapping("/{id}")
    public UserDto getUserById(@PathVariable long id) {
        return userServiceImpl.getUserById(id);
    }

    @GetMapping
    public List<UserDto> getAllUsers() {
        return userServiceImpl.getAllUsers();
    }

    @PatchMapping("/{id}")
    public UserDto updateUser(@PathVariable long id, @RequestBody User user) {
        return userServiceImpl.updateUser(id, user);
    }

    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable long id) {
        userServiceImpl.deleteUser(id);
    }

}
