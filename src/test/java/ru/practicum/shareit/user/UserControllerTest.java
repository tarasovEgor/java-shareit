package ru.practicum.shareit.user;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.AutoConfigureJsonTesters;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.user.controller.UserController;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.impl.UserServiceImpl;

import java.util.List;

import static org.assertj.core.api.BDDAssertions.then;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@AutoConfigureJsonTesters
@WebMvcTest(UserController.class)
public class UserControllerTest {

    @MockBean
    private UserServiceImpl userService;

    @Autowired
    MockMvc mockMvc;

    @Autowired
    private JacksonTester<User> jsonRequestAttempt;

    @Autowired
    private JacksonTester<UserDto> jsonResultAttempt;

    @Test
    void shouldPostUser() throws Exception {
        // Given
        User user = new User("user1", "user1@mail.com");
        UserDto userDto = new UserDto(1L, "user1", "user1@mail.com");

        given(userService
                .saveUser(user))
                .willReturn(userDto);

        // When
        MockHttpServletResponse response = mockMvc.perform(
                post("/users").contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequestAttempt.write(user).getJson()))
                .andReturn().getResponse();

        // Then
        then(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        then(response.getContentAsString()).isEqualTo(
                jsonResultAttempt.write(
                        userDto
                ).getJson()
        );
    }

    @Test
    void shouldGetUserById() throws Exception {
        // Given
        UserDto userDto = new UserDto(1L, "user1", "user1@mail.com");

        given(userService
                .getUserById(1L))
                .willReturn(userDto);

        // When
        MockHttpServletResponse response = mockMvc.perform(
                get("/users/1").accept(MediaType.APPLICATION_JSON)
        ).andReturn().getResponse();

        // Then
        then(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        then(response.getContentAsString()).isEqualTo(
                jsonResultAttempt.write(
                        userDto
                ).getJson()
        );
    }

    @Test
    void shouldGetAllUsers() throws Exception {
        // Given
        UserDto userDto1 = new UserDto(1L, "user1", "user1@mail.com");
        UserDto userDto2 = new UserDto(2L, "user2", "user2@mail.com");
        UserDto userDto3 = new UserDto(3L, "user3", "user3@mail.com");

        given(userService
                .getAllUsers())
                .willReturn(List.of(userDto1, userDto2, userDto3));

       // List<UserDto> checkList = Arrays.asList(userDto1, userDto2, userDto3);

        // When
        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(3)))
                .andExpect(jsonPath("$[0].id", is(userDto1.getId()), Long.class))
                .andExpect(jsonPath("$[0].name", is(userDto1.getName())))
                .andExpect(jsonPath("$[0].email", is(userDto1.getEmail())));
    }

    @Test
    void shouldPatchUpdateUserById() throws Exception {
        // Given
        User updatedUser = new User("updated", "updated@mail.com");
        UserDto updatedUserDto = new UserDto(1L, "updated", "updated@mail.com");

        given(userService
                .updateUser(1L, updatedUser))
                .willReturn(updatedUserDto);

        // When
        MockHttpServletResponse response = mockMvc.perform(
                patch("/users/1").contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequestAttempt.write(updatedUser).getJson())
        ).andReturn().getResponse();

        // Then
        then(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        then(response.getContentAsString()).isEqualTo(
                jsonResultAttempt.write(
                        updatedUserDto
                ).getJson()
        );
    }

    @Test
    void shouldDeleteUserById() throws Exception {
        // Given
        User user1 = new User("user1", "user1@mail.com");
        UserDto userDto = new UserDto(1L, "user1", "user1@mail.com");

        given(userService
                .saveUser(user1))
                .willReturn(userDto);

        // When
        MockHttpServletResponse response = mockMvc.perform(
                delete("/users/1").accept(MediaType.APPLICATION_JSON)
        ).andReturn().getResponse();

        // Then
        then(response.getStatus()).isEqualTo(HttpStatus.OK.value());
    }

}
