package ru.practicum.shareit.error;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.Mock;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.AutoConfigureJsonTesters;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import ru.practicum.shareit.exception.InvalidEmailException;
import ru.practicum.shareit.exception.UserDoesNotExistException;
import ru.practicum.shareit.user.controller.UserController;
import ru.practicum.shareit.user.model.User;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Disabled
@ExtendWith(SpringExtension.class)
@AutoConfigureJsonTesters
@WebMvcTest(ErrorHandler.class)
public class ErrorHandlerUserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Mock
    private UserController userController;

    @Autowired
    private JacksonTester<User> jsonRequestAttemptUser;

    private User user;

    @BeforeEach
    void setUp() {

        this.mockMvc = MockMvcBuilders
                .standaloneSetup(new ErrorHandler(), userController)
                .build();

        user = new User(
                "user",
                "user@mail.com"
        );

    }

    @Test
    void checkInvalidEmailExceptionsAreCaughtAndStatusIs400() throws Exception {

        when(userController
                .saveUser(user))
                .thenThrow(new InvalidEmailException("Invalid email exception"));

        mockMvc.perform(
                post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequestAttemptUser.write(user).getJson()))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Invalid email exception"));

    }

    @Test
    void checkUserDoesNotExistExceptionsAreCaughtAndStatusIs404() throws Exception {

        when(userController
                .getUserById(999L))
                .thenThrow(new UserDoesNotExistException("User doesn't exist exception"));

        mockMvc.perform(
                get("/users/999")
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("User doesn't exist exception"));

    }

}
