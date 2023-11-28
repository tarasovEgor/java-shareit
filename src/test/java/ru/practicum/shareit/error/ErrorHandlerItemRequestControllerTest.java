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
import ru.practicum.shareit.exception.InvalidItemRequestDescriptionException;
import ru.practicum.shareit.exception.ItemRequestDoesNotExistException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.controller.ItemRequestController;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
@ExtendWith(SpringExtension.class)
@AutoConfigureJsonTesters
@WebMvcTest(ErrorHandler.class)
public class ErrorHandlerItemRequestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Mock
    private ItemRequestController itemRequestController;

    @Autowired
    private JacksonTester<ItemRequest> jsonRequestAttemptItemRequest;

    private User itemOwner;
    private User requestor;
    private Item item;
    private ItemRequest itemRequest;

    @BeforeEach
    void setUp() {

        this.mockMvc = MockMvcBuilders
                .standaloneSetup(new ErrorHandler(), itemRequestController)
                .build();

        itemOwner = new User(
                "owner",
                "owner@mail.com"
        );

        requestor = new User(
                "requestor",
                "requestor@mail.com"
        );

        item = new Item(
                "item",
                "desc",
                true,
                itemOwner,
                1L
        );

        itemOwner.setId(1L);
        requestor.setId(2L);
        item.setId(1L);

    }

    @Test
    void checkInvalidItemRequestDescriptionExceptionsAreCaughtAndStatus400() throws Exception {
        // Given
        itemRequest = new ItemRequest(
                null,
                requestor
        );

        // When
        when(itemRequestController
                .saveItemRequest(itemRequest, 2L))
                .thenThrow(new InvalidItemRequestDescriptionException("Invalid item request description exception"));

        mockMvc.perform(
                post("/requests")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequestAttemptItemRequest.write(itemRequest).getJson())
                        .header("X-Sharer-User-Id", 2))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error")
                        .value("Invalid item request description exception"));

    }

    @Test
    void checkItemRequestDoesNotExistExceptionsAreCaughtAndStatus404() throws Exception {
        // When
        when(itemRequestController
                .getItemRequestById(1L, 2L))
                .thenThrow(new ItemRequestDoesNotExistException("Item request does not exist exception"));

        mockMvc.perform(
                get("/requests/1")
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 2))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error")
                        .value("Item request does not exist exception"));
    }

}
