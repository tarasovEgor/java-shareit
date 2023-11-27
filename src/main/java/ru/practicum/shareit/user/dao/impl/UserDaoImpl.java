//package ru.practicum.shareit.user.dao.impl;
//
//import lombok.extern.slf4j.Slf4j;
//
//import org.springframework.stereotype.Repository;
//
//import ru.practicum.shareit.user.dao.UserDao;
//import ru.practicum.shareit.user.model.User;
//import ru.practicum.shareit.validation.UserValidation;
//
//import java.util.*;
//
//@Repository
//@Slf4j
//public class UserDaoImpl implements UserDao {
//
//    private Map<Long, User> users = new HashMap<>();
//    private long id = 1;
//
//    @Override
//    public User saveUser(User user) {
//        UserValidation.isUserEmailValid(user, users);
//        user.setId(id++);
//        users.put(user.getId(), user);
//        return user;
//    }
//
//    @Override
//    public Optional<User> getUser(long id) {
//        return Optional.ofNullable(users.get(id));
//    }
//
//    @Override
//    public List<User> getAllUsers() {
//        return new ArrayList<>(users.values());
//    }
//
//    @Override
//    public User updateUser(long id, User user) {
//        Optional<User> optionalUser = Optional.ofNullable(users.get(id));
//        return UserValidation
//                .isUserValidForUpdate(user, optionalUser, users).get();
//    }
//
//    @Override
//    public void deleteUser(long id) {
//        users.remove(id);
//    }
//}
