package ru.practicum.shareit.error;

import org.junit.jupiter.api.BeforeEach;
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

import ru.practicum.shareit.exception.*;
import ru.practicum.shareit.item.controller.ItemController;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@AutoConfigureJsonTesters
@WebMvcTest(ErrorHandler.class)
public class ErrorHandlerItemControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Mock
    private ItemController itemController;

    @Autowired
    private JacksonTester<Item> jsonRequestAttemptItem;

    @Autowired
    private JacksonTester<Comment> jsonRequestAttemptComment;

    private User itemOwner;
    private Item item;

    @BeforeEach
    void setUp() {

        this.mockMvc = MockMvcBuilders
                .standaloneSetup(new ErrorHandler(), itemController)
                .build();

        itemOwner = new User(
                "user",
                "user@mail.com"
        );

        item = new Item(
                "item",
                "desc",
                true,
                itemOwner,
                1L
        );

        itemOwner.setId(1L);
        item.setId(1L);

    }

    @Test
    void checkInvalidItemNameExceptionsAreCaughtAndStatusIs400() throws Exception {
        // Given
        Item item = new Item(
                "",
                "desc",
                true,
                itemOwner,
                1L
        );

        // When
        when(itemController
                .saveItem(item, 1L))
                .thenThrow(new InvalidItemNameException("Invalid item name exception"));

        // Then
        mockMvc.perform(
                post("/items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequestAttemptItem.write(item).getJson())
                        .header("X-Sharer-User-Id", 1))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error")
                        .value("Invalid item name exception"));

    }

    @Test
    void checkInvalidItemDescriptionExceptionsAreCaughtAndStatusIs400() throws Exception {
        // Given
        Item item = new Item(
                "item",
                null,
                true,
                itemOwner,
                1L
        );

        // When
        when(itemController
                .saveItem(item, 1L))
                .thenThrow(new InvalidItemDescriptionException("Invalid item description exception"));

        // Then
        mockMvc.perform(
                        post("/items")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(jsonRequestAttemptItem.write(item).getJson())
                                .header("X-Sharer-User-Id", 1))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error")
                        .value("Invalid item description exception"));

    }

    @Test
    void checkInvalidAvailableFieldExceptionsAreCaughtAndStatusIs400() throws Exception {
        // Given
        Item item = new Item(
                "item",
                "desc",
                null,
                itemOwner,
                1L
        );

        // When
        when(itemController
                .saveItem(item, 1L))
                .thenThrow(new InvalidAvailableFieldException("Invalid item available exception"));

        // Then
        mockMvc.perform(
                        post("/items")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(jsonRequestAttemptItem.write(item).getJson())
                                .header("X-Sharer-User-Id", 1))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error")
                        .value("Invalid item available exception"));

    }

    @Test
    void checkItemDoesNotExistExceptionsAreCaughtAndStatusIs404() throws Exception {
        // When
        when(itemController
                .getItem(999L, 1L))
                .thenThrow(new ItemDoesNotExistException("Item doesn't exist exception"));

        mockMvc.perform(
                get("/items/999")
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error")
                        .value("Item doesn't exist exception"));

    }

    @Test
    void checkNoBookingForCommentExceptionsAreCaughtAndStatus400() throws Exception {
        // Given
        User author = new User(
                "author",
                "author@mail.com"
        );

        author.setId(2L);

        Comment comment = new Comment(
                1L,
                "text",
                item,
                author,
                LocalDateTime.of(2023, 11, 12, 12, 12)
        );

        // When
        when(itemController
                .saveComment(comment, 1L, 2L))
                .thenThrow(new NoBookingForCommentException("No booking for comment exception"));

        mockMvc.perform(
                post("/items/1/comment")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequestAttemptComment.write(comment).getJson())
                        .header("X-Sharer-User-Id", 2))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error")
                        .value("No booking for comment exception"));
    }

    @Test
    void checkInvalidCommentExceptionsAreCaughtAndStatus400() throws Exception {
        // Given
        User author = new User(
                "author",
                "author@mail.com"
        );

        author.setId(2L);

        Comment comment = new Comment(
                1L,
                "",
                item,
                author,
                LocalDateTime.of(2023, 11, 12, 12, 12)
        );

        // When
        when(itemController
                .saveComment(comment, 1L, 2L))
                .thenThrow(new InvalidCommentException("Invalid comment exception"));

        mockMvc.perform(
                post("/items/1/comment")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequestAttemptComment.write(comment).getJson())
                        .header("X-Sharer-User-Id", 2))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error")
                        .value("Invalid comment exception"));
    }
}
