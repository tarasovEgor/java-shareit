package ru.practicum.shareit.user.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ru.practicum.shareit.user.dao.UserDao;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    private UserDao userDao;

    @Autowired
    public UserService(UserDao userDao) {
        this.userDao = userDao;
    }

    public User saveUser(User user) {
        return userDao.saveUser(user);
    }

    public Optional<User> getUserById(long id) {
        return userDao.getUser(id);
    }

    public List<User> getAllUsers() {
        return userDao.getAllUsers();
    }

    public User updateUser(long id, User user) {
        return userDao.updateUser(id, user);
    }

    public void deleteUser(long id) {
        userDao.deleteUser(id);
    }

}
