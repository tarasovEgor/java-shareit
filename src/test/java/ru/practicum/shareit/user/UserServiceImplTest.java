package ru.practicum.shareit.user;

import org.junit.jupiter.api.BeforeEach;
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
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.impl.UserServiceImpl;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.*;


//@Transactional
//@SpringBootTest(
//        properties = "db.name=test",
//        webEnvironment = SpringBootTest.WebEnvironment.NONE)
//@RequiredArgsConstructor(onConstructor_ = @Autowired)
//@ExtendWith(SpringExtension.class)
//@DirtiesContext
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










































    /*@MockBean
    private EntityManager em;

    @MockBean
    private UserService userService;

    @Test
    public void shouldSaveUser() {
        //Given
        User user = new User("user", "user@mail.com");

        //When
        UserDto userDto = userService.saveUser(user);

        //Then
        TypedQuery<User> query = em.createQuery("Select u from User u where u.email = :email", User.class);
        User savedUser = query.setParameter("email", userDto.getEmail())
                .getSingleResult();

        assertThat(savedUser.getId(), notNullValue());
        assertThat(savedUser.getName(), equalTo(userDto.getName()));
        assertThat(savedUser.getEmail(), equalTo(userDto.getEmail()));
    }

    @Test
    public void shouldNotSaveUserWhenEmailDoesNotContainAtSign() {
        //Given
        User user = new User("user", "usermail.com");

        //When
        InvalidEmailException exception = assertThrows(InvalidEmailException.class, () -> {
            UserDto userDto = userService.saveUser(user);
        });

        //Then
        assertThat(exception.getMessage()).isEqualTo("Please, enter a valid email address.");
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
    }

    @Test
    public void shouldGetUserById() {
        //Given
        User user = new User("user", "user@gmail.com");
        em.persist(user);

        //When
        UserDto savedUser = userService.getUserById(1L);

        //Then
        assertThat(savedUser.getId()).isEqualTo(1L);
        assertThat(savedUser.getName()).isEqualTo(user.getName());
        assertThat(savedUser.getEmail()).isEqualTo(user.getEmail());
    }

    @Test
    public void shouldNotGetUserByNonExistentId() {
        //Given
        User user = new User("user", "user@gmail.com");
        em.persist(user);

        //When
        UserDoesNotExistException exception = assertThrows(UserDoesNotExistException.class, () -> {
            UserDto userDto = userService.getUserById(999L);
        });

        //Then
        assertThat(exception.getMessage()).isEqualTo("User doesn't exist.");
    }

    @Test
    public void shouldGetAllUsers() {
        //Given
        User user1 = new User("user1", "user1@gmail.com");
        em.persist(user1);
        User user2 = new User("user2", "user2@gmail.com");
        em.persist(user2);
        User user3 = new User("user3", "user3@gmail.com");
        em.persist(user3);

        //When
        List<UserDto> users = userService.getAllUsers();

        //Then
        assertThat(users.size()).isEqualTo(3L);
        assertThat(users).asList().contains(UserMapper.toUserDto(user1));
        assertThat(users).asList().contains(UserMapper.toUserDto(user2));
        assertThat(users).asList().contains(UserMapper.toUserDto(user3));
    }

    @Test
    public void shouldUpdateUserById() {
        //Given
        User user1 = new User("user1", "user1@mail.com");
        em.persist(user1);

        User user2 = new User("user2", "user2@mail.com");
        em.persist(user2);

        User updatedUser = new User("updated", "updated@mail.com");
        updatedUser.setId(2L);

        //When
        userService.saveUser(user1);
        userService.saveUser(user2);

        UserDto checkUser = userService.updateUser(2L, updatedUser);

        //Then
        assertThat(checkUser.getId()).isEqualTo(updatedUser.getId());
        assertThat(checkUser.getName()).isEqualTo(updatedUser.getName());
        assertThat(checkUser.getEmail()).isEqualTo(updatedUser.getEmail());
    }


    @Test
    public void shouldDeleteUserById() {
        //Given
        User user1 = new User("user1", "user1@gmail.com");
        em.persist(user1);

        User user2 = new User("user2", "user2@mail.com");
        em.persist(user2);

        User user3 = new User("user3", "user3@mail.com");
        em.persist(user3);

        //When
        userService.deleteUser(user1.getId());

        //Then
        List<UserDto> users = userService.getAllUsers();

        assertThat(users.size()).isEqualTo(2);
        assertThat(users.get(0).getName()).isEqualTo(user2.getName());
        assertThat(users.get(1).getName()).isEqualTo(user3.getName());
    }*/
}


















//--------------USER UPDATE JUST IN CASE
//Given
//        User user1 = new User("user1", "user1@mail.com");
//        em.persist(user1);
//
//        User user2 = new User("user2", "user2@mail.com");
//        em.persist(user2);
//
//        User updatedUser = new User("updated", "updated@mail.com");
//        updatedUser.setId(2L);
//
//        User user = UserMapper.toUser(userService.getUserById(user2.getId()));
//        user.setId(updatedUser.getId());
//        user.setName(updatedUser.getName());
//        user.setEmail(updatedUser.getEmail());
//        userService.saveUser(user);
//
//        User checkUser = UserMapper.toUser(userService.getUserById(user2.getId()));
//        checkUser.setId(user2.getId());
//
//
//        assertThat(checkUser.getId()).isEqualTo(updatedUser.getId());
//        assertThat(checkUser.getName()).isEqualTo(updatedUser.getName());
//        assertThat(checkUser.getEmail()).isEqualTo(updatedUser.getEmail());