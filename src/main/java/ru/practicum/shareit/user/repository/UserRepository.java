package ru.practicum.shareit.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.user.model.User;

public interface UserRepository extends JpaRepository<User, Long> {

    @Modifying
    @Query("update User u" +
            " set u.name = ?1," +
            " u.email = ?2" +
            " where u.id = ?3")
    void updateUser(String name, String email, Long userId);

    @Modifying
    @Query("update User u" +
            " set u.name = ?1" +
            " where u.id = ?2")
    void updateUserName(String name, Long userId);

    @Modifying
    @Query("update User u" +
            " set u.email = ?1" +
            " where u.id = ?2")
    void updateUserEmail(String email, Long userId);


}
