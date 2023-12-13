package ru.practicum.shareit.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import ru.practicum.shareit.exception.InvalidEmailException;
import ru.practicum.shareit.exception.UserDoesNotExistException;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.impl.UserServiceImpl;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.*;

@Disabled
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserServiceImpl userService;

    private User user;

    @BeforeEach
    public void setup() {
        userRepository = Mockito.mock(UserRepository.class);
        userService = new UserServiceImpl(userRepository);
        user = new User(
                "user",
                "user@mail.com"
        );
        user.setId(1L);
    }

    @Test
    void shouldSaveUser() {
        // Given
        given(userRepository.save(user)).willReturn(user);

        // When
        UserDto savedUser = userService.saveUser(user);

        // Then
        assertThat(savedUser).isNotNull();
        assertThat(savedUser.getName()).isEqualTo(user.getName());
        assertThat(savedUser.getEmail()).isEqualTo(user.getEmail());
    }

    @Test
    void shouldNotSaveUserWhenEmailDoesNotContainAtSign() {
        //Given
        User user1 = new User("user", "usermail.com");

        //When
        InvalidEmailException exception = assertThrows(InvalidEmailException.class, () -> {
            UserDto userDto = userService.saveUser(user1);
        });

        //Then
        assertThat(exception.getMessage()).isEqualTo("Please, enter a valid email address.");
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    public void shouldNotSaveUserWhenEmailIsEmpty() {
        //Given
        User user = new User("user", null);

        //When
        InvalidEmailException exception = assertThrows(InvalidEmailException.class, () -> {
            UserDto userDto = userService.saveUser(user);
        });

        //Then
        assertThat(exception.getMessage()).isEqualTo("Please, enter an email address.");
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void shouldGetUserById() {
        // Given
        given(userRepository.findById(1L)).willReturn(Optional.of(user));

        // When
        UserDto userDto = userService.getUserById(user.getId());

        // Then
        assertThat(userDto).isNotNull();
        assertThat(userDto.getName()).isEqualTo(user.getName());
        assertThat(userDto.getEmail()).isEqualTo(user.getEmail());
    }

    @Test
    public void shouldNotGetUserByNonExistentId() {
        //When
        UserDoesNotExistException exception = assertThrows(UserDoesNotExistException.class, () -> {
            UserDto userDto = userService.getUserById(999L);
        });

        //Then
        assertThat(exception.getMessage()).isEqualTo("User doesn't exist.");
    }

    @Test
    void shouldGetAllUsers() {
        // Given
        User addinionalUser = new User("additional", "additional@mail.com");

        given(userRepository.findAll()).willReturn(List.of(user, addinionalUser));

        // When
        List<UserDto> userDtoList = userService.getAllUsers();

        // Then
        assertThat(userDtoList).isNotNull();
        assertThat(userDtoList.size()).isEqualTo(2);
    }

    @Test
    void shouldUpdateUserById() {
        // Given
        given(userRepository.save(user)).willReturn(user);
        userRepository.save(user);

        user.setName("updated");
        user.setEmail("updated@mail.com");

        // When
        UserDto updatedUserDto = userService.updateUser(user.getId(), user);

        // Then
        assertThat(updatedUserDto.getEmail()).isEqualTo("updated@mail.com");
        assertThat(updatedUserDto.getName()).isEqualTo("updated");
    }

    @Test
    void shouldDeleteUserById() {
        // Given
        User userToDelete = new User("user", "user@mail.com");

        // When
        when(userRepository.findById(1L)).thenReturn(Optional.of(userToDelete));

        // Then
        assertAll(() -> userService.deleteUser(1L));
    }

    @Test
    void shouldMapUserDtoToUser() {

        UserDto userDto = new UserDto(
                1L,
                "user",
                "user@mail.com"
        );

        User user = UserMapper.toUser(userDto);

        assertNotNull(user);
        assertThat(user.getName()).isEqualTo(userDto.getName());
        assertThat(user.getEmail()).isEqualTo(userDto.getEmail());
    }

}
