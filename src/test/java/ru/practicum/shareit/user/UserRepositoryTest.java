package ru.practicum.shareit.user;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.annotation.DirtiesContext;

import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@DataJpaTest
@DirtiesContext
public class UserRepositoryTest {

    @Autowired
    TestEntityManager entityManager;

    @Autowired
    UserRepository userRepository;

    User user;

    @BeforeEach
    public void setUp() {
        user = new User(
                "user",
                "example@mail.ru"
        );
    }

    @AfterEach
    public void destruct() {
        userRepository.deleteAll();
    }

    @Test
    public void shouldFindNoUsersIfRepositoryIsEmpty() {
        List<User> tutorials = userRepository.findAll();

        assertThat(tutorials).isEqualTo(List.of());
    }

    @Test
    public void shouldSaveUser() {
        //When
        User newUser = userRepository.save(user);
        List<User> users = userRepository.findAll();

        //Then
        assertThat(newUser).hasFieldOrPropertyWithValue("name", "user");
        assertThat(newUser).hasFieldOrPropertyWithValue("email", "example@mail.ru");
        assertThat(users.size()).isEqualTo(1);
    }

    @Test
    public void shouldFindAllUsers() {
        //Given
        User user1 = new User("user1", "user1@mail.com");
        entityManager.persist(user1);

        User user2 = new User("user2", "user2@mail.com");
        entityManager.persist(user2);

        User user3 = new User("user3", "user3@mail.com");
        entityManager.persist(user3);

        //When
        Iterable<User> users = userRepository.findAll();

        //Then
        assertThat(users).asList().hasSize(3);
    }

    @Test
    public void shouldGetUserById() {
        //Given
        User user1 = new User("user1", "user1@mail.com");
        entityManager.persist(user1);

        User user2 = new User("user2", "user2@mail.com");
        entityManager.persist(user2);

        //When
        User foundTutorial = userRepository.findById(user2.getId()).get();

        //Then
        assertThat(foundTutorial).isEqualTo(user2);
    }

    @Test
    public void shouldUpdateUser() {
        //Given
        User user1 = new User("user1", "user1@mail.com");
        entityManager.persist(user1);

        User user2 = new User("user2", "user2gmail.com");
        entityManager.persist(user2);

        User updatedUser = new User("updated", "update@mail.com");

        //When
        User user = userRepository.findById(user2.getId()).get();
        user.setName(updatedUser.getName());
        user.setEmail(updatedUser.getEmail());
        userRepository.save(user);

        User checkUser = userRepository.findById(user2.getId()).get();

        //Then
        assertThat(checkUser.getId()).isEqualTo(user2.getId());
        assertThat(checkUser.getName()).isEqualTo(updatedUser.getName());
        assertThat(checkUser.getEmail()).isEqualTo(updatedUser.getEmail());
    }

    @Test
    public void shouldDeleteUserById() {
        //Given
        User user1 = new User("user1", "user1@mail.com");
        entityManager.persist(user1);

        User user2 = new User("user2", "user2gmail.com");
        entityManager.persist(user2);

        User user3 = new User("user3", "user3@gmail.com");
        entityManager.persist(user3);

        //When
        userRepository.deleteById(user2.getId());

        Iterable<User> tutorials = userRepository.findAll();

        //Then
        assertThat(tutorials).asList().hasSize(2).contains(user1, user3);
    }

    @Test
    public void shouldDeleteAllUsers() {
        //Given
        entityManager.persist(new User("user1", "user1@gmail.com"));
        entityManager.persist(new User("user2", "user2@gmail.com"));

        //When
        userRepository.deleteAll();

        //Then
        assertThat(userRepository.findAll()).asList().isEmpty();
    }

}
