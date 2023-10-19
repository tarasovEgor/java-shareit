package ru.practicum.shareit.user.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ru.practicum.shareit.exception.UserDoesNotExistException;
import ru.practicum.shareit.user.dao.UserDao;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.validation.UserValidation;

import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository repository;

    @Autowired
    public UserServiceImpl(UserRepository repository) {
        this.repository = repository;
    }

    @Override
    public UserDto saveUser(User user) {
        UserValidation.isUserEmailValid(user);
        return UserMapper.toUserDto(repository.save(user));
    }

    @Override
    public UserDto getUserById(long id) {
        Optional<User> user = repository.findById(id);
        if (user.isEmpty()) throw new UserDoesNotExistException("User doesn't exist.");
        else return UserMapper.toUserDto(user.get());
    }

    @Override
    public List<UserDto> getAllUsers() {
        return UserMapper.toUserDto(repository.findAll());
    }

    @Override
    @Transactional
    public UserDto updateUser(long id, User user) {
        if (user.getName() != null && user.getEmail() != null) {
            repository.updateUser(user.getName(), user.getEmail(), id);
        } else if (user.getName() != null) {
            repository.updateUserName(user.getName(), id);
            user.setEmail(repository.getReferenceById(id).getEmail());
        } else if (user.getEmail() != null) {
            repository.updateUserEmail(user.getEmail(), id);
            user.setName(repository.getReferenceById(id).getName());
        }
        user.setId(id);
        return UserMapper.toUserDto(user);
    }

    @Override
    public void deleteUser(long id) {
        User user = repository.getReferenceById(id);
        repository.delete(user);
    }

}
