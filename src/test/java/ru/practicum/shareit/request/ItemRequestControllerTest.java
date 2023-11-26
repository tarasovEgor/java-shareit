package ru.practicum.shareit.request;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.AutoConfigureJsonTesters;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.controller.ItemRequestController;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.BDDAssertions.then;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@AutoConfigureJsonTesters
@WebMvcTest(ItemRequestController.class)
public class ItemRequestControllerTest {

    @MockBean
    private ItemRequestService itemRequestService;

    @Autowired
    private MockMvc mockMvc;

    private ItemRequest itemRequest;
    private ItemRequestDto itemRequestDto;
    private Item item;
    private User itemOwner;
    private User requestor;

    @Autowired
    private JacksonTester<ItemRequest> jsonRequestAttemptItemRequest;

    @Autowired
    private JacksonTester<ItemRequestDto> jsonResultAttemptItemRequestDto;

    @BeforeEach
    void setUp() {

        itemOwner = new User(
                "itemOwner",
                "itemOwner@mail.com"
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
                null
        );

        itemRequest = new ItemRequest(
                "item description",
                itemOwner
        );

        itemRequestDto = new ItemRequestDto(
                1L,
                "item description",
                requestor,
                LocalDateTime.of(2023, 11, 23, 12, 23, 0),
                List.of(item)
        );

        itemOwner.setId(1L);
        requestor.setId(2L);
        item.setId(1L);

    }

    @Test
    void shouldPostSaveItemRequest() throws Exception {
        // Given
        given(itemRequestService
                .saveItemRequest(itemRequest, 2L))
                .willReturn(itemRequestDto);

        // When
        MockHttpServletResponse response = mockMvc.perform(
                post("/requests")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequestAttemptItemRequest.write(itemRequest).getJson())
                        .header("X-Sharer-User-Id", 2)
        ).andReturn().getResponse();

        // Then
        then(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        then(response.getContentAsString()).isEqualTo(
                jsonResultAttemptItemRequestDto.write(itemRequestDto).getJson()
        );

    }

    @Test
    void shouldGetAllItemRequestsByUser() throws Exception {
        // Given
        given(itemRequestService
                .getAllItemRequestsByUser(anyLong()))
                .willReturn(List.of(itemRequestDto));

        // When
        mockMvc.perform(
                get("/requests")
                        .contentType(MediaType.APPLICATION_JSON)
                        //.content(jsonRequestAttemptItemRequest.write(itemRequest).getJson())
                        .header("X-Sharer-User-Id", 2))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].description", is(itemRequestDto.getDescription())))
                .andExpect(jsonPath("$[0].requestor", is(itemRequestDto.getRequestor()), User.class))
                .andExpect(jsonPath("$[0].items[0]", is(item), Item.class));

    }

    @Test
    void shouldGetItemRequestById() throws Exception {
        // Given
        given(itemRequestService
                .getItemRequestById(anyLong(), anyLong()))
                .willReturn(itemRequestDto);

        // When
        MockHttpServletResponse response = mockMvc.perform(
                get("/requests/1")
                      //  .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 2))
                .andReturn().getResponse();

        // Then
        then(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        then(response.getContentAsString()).isEqualTo(
                jsonResultAttemptItemRequestDto.write(itemRequestDto).getJson()
        );

    }

    @Test
    void shouldGetAllItemRequests() throws Exception {
        // Given
        given(itemRequestService
                .getAllItemRequests(PageRequest.of(0, 5), 2))
                .willReturn(List.of(itemRequestDto));

        // When
        mockMvc.perform(
                get("/requests/all?from=0&size=5")
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 2))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(itemRequestDto.getId()), Long.class))
                .andExpect(jsonPath("$[0].description", is(itemRequestDto.getDescription())))
                .andExpect(jsonPath("$[0].requestor", is(itemRequestDto.getRequestor()), User.class))
                .andExpect(jsonPath("$[0].items[0]", is(item), Item.class));

    }
}
