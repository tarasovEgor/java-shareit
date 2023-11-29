package ru.practicum.shareit.item;

import org.junit.jupiter.api.BeforeEach;
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

import ru.practicum.shareit.item.controller.ItemController;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemWithBookingDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.BDDAssertions.then;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@AutoConfigureJsonTesters
@WebMvcTest(ItemController.class)
public class ItemControllerTest {

    @MockBean
    private ItemService itemService;

    @MockBean
    private UserService userService;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JacksonTester<Item> jsonRequestAttemptItem;

    @Autowired
    private JacksonTester<User> jsonRequestAttemptUser;

    @Autowired
    private JacksonTester<Comment> jsonRequestAttemptComment;

    @Autowired
    private JacksonTester<ItemDto> jsonResultAttemptItem;

    @Autowired
    private JacksonTester<ItemWithBookingDto> jsonResultAttemptItemWithBookingDto;

    @Autowired
    private JacksonTester<CommentDto> jsonResultAttemptCommentDto;

    private User user;
    private UserDto userDto;
    private Item item;
    private ItemDto itemDto;

    @BeforeEach
    void setUp() {

        user = new User(
                "user1",
                "user1@mail.com"
        );

        userDto = new UserDto(
                1L,
                "user1",
                "user1@mail.com"
        );

        item = new Item(
                "item",
                "description",
                false,
                null,
                null
        );

        itemDto = new ItemDto(
                1L,
                "item",
                "description",
                false,
                null
        );

    }

    @Test
    void shouldPostItem() throws Exception {
        // Given
        given(userService
                .saveUser(user))
                .willReturn(userDto);

        given(itemService
                .saveItem(item, 1L))
                .willReturn(itemDto);

        // When
        mockMvc.perform(
                post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequestAttemptUser.write(user).getJson()))
                .andReturn().getResponse();

        MockHttpServletResponse response = mockMvc.perform(
                post("/items").contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequestAttemptItem.write(item).getJson())
                        .header("X-Sharer-User-Id", 1))
                .andReturn().getResponse();

        // Then
        then(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        then(response.getContentAsString()).isEqualTo(
                jsonResultAttemptItem.write(
                        itemDto
                ).getJson()
        );
    }

    @Test
    void shouldGetItemById() throws Exception {
        // Given
        ItemWithBookingDto itemWithBookingDto = new ItemWithBookingDto(
                1L,
                "item",
                "description",
                false,
                null,
                null,
                null,
                null
                );

        given(userService
                .saveUser(user))
                .willReturn(userDto);

        given(itemService
                .getItemById(1L, 1L))
                .willReturn(itemWithBookingDto);

        MockHttpServletResponse response = mockMvc.perform(
                get("/items/1").accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1))
                .andReturn()
                .getResponse();

        // Then
        then(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        then(response.getContentAsString()).isEqualTo(
                jsonResultAttemptItemWithBookingDto.write(
                        itemWithBookingDto
                ).getJson()
        );
    }

    @Test
    void shouldGetAllUsers() throws Exception {
        // Given
        ItemWithBookingDto itemWithBookingDto1 = new ItemWithBookingDto(
                1L,
                "item",
                "description",
                false,
                null,
                null,
                null,
                null
        );

        ItemWithBookingDto itemWithBookingDto2 = new ItemWithBookingDto(
                2L,
                "item",
                "description",
                false,
                null,
                null,
                null,
                null
        );

        ItemWithBookingDto itemWithBookingDto3 = new ItemWithBookingDto(
                3L,
                "item",
                "description",
                false,
                null,
                null,
                null,
                null
        );

        given(itemService
                .getAllItems(1L))
                .willReturn(List.of(itemWithBookingDto1, itemWithBookingDto2, itemWithBookingDto3));

        // When
        mockMvc.perform(
                post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequestAttemptUser.write(user).getJson()))
                .andReturn()
                .getResponse();

        mockMvc.perform(
                get("/items")
                        .header("X-Sharer-User-Id", 1)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(3)))
                .andExpect(jsonPath("$[0].id", is(itemWithBookingDto1.getId()), Long.class))
                .andExpect(jsonPath("$[0].name", is(itemWithBookingDto1.getName())))
                .andExpect(jsonPath("$[0].description", is(itemWithBookingDto1.getDescription())))
                .andExpect(jsonPath("$[1].id", is(itemWithBookingDto2.getId()), Long.class))
                .andExpect(jsonPath("$[1].name", is(itemWithBookingDto2.getName())))
                .andExpect(jsonPath("$[1].description", is(itemWithBookingDto2.getDescription())))
                .andExpect(jsonPath("$[2].id", is(itemWithBookingDto3.getId()), Long.class))
                .andExpect(jsonPath("$[2].name", is(itemWithBookingDto3.getName())))
                .andExpect(jsonPath("$[2].description", is(itemWithBookingDto3.getDescription())));
    }

    @Test
    void shouldPatchUpdateItemById() throws Exception {
        // Given
        Item updatedItem = new Item(
                "updated",
                "updated",
                true,
                user,
                null
        );

        ItemDto updatedItemDto = new ItemDto(
                1L,
                "updated",
                "updated",
                true,
                null
        );

        given(itemService
                .updateItem(updatedItem, 1L, 1L))
                .willReturn(updatedItemDto);

        // When
        MockHttpServletResponse response = mockMvc.perform(
                patch("/items/1").contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequestAttemptItem.write(updatedItem).getJson())
                        .header("X-Sharer-User-Id", 1)
        ).andReturn().getResponse();

        // Then
        then(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        then(response.getContentAsString()).isEqualTo(
                jsonResultAttemptItem.write(
                        updatedItemDto
                ).getJson()
        );
    }

    @Test
    void shouldGetSearchItem() throws Exception {
        // Given
        ItemDto searchedItem1 = new ItemDto(
                1L,
                "search",
                "searching",
                true,
                null
        );

        ItemDto searchedItem2 = new ItemDto(
                2L,
                "sears",
                "garden sears",
                false,
                null
        );

        ItemDto searchedItem3 = new ItemDto(
                3L,
                "glass",
                "a kitchen glass",
                false,
                null
        );

        given(itemService
                .searchItem("sear"))
                .willReturn(List.of(searchedItem1, searchedItem2));

        // When
        mockMvc.perform(
                get("/items/search")
                        .accept(MediaType.APPLICATION_JSON)
                        .param("text", "sear"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(searchedItem1.getId()), Long.class))
                .andExpect(jsonPath("$[0].name", is(searchedItem1.getName())))
                .andExpect(jsonPath("$[0].description", is(searchedItem1.getDescription())))
                .andExpect(jsonPath("$[1].id", is(searchedItem2.getId()), Long.class))
                .andExpect(jsonPath("$[1].name", is(searchedItem2.getName())))
                .andExpect(jsonPath("$[1].description", is(searchedItem2.getDescription())));
    }

    @Test
    void shouldPostComment() throws Exception {
        // Given
        Comment comment = new Comment(1L, "text", item, user, LocalDateTime.now());

        CommentDto commentDto = new CommentDto(1L, "text", item, user.getName());

        given(userService
                .saveUser(user))
                .willReturn(userDto);

        given(itemService
                .saveItem(item, 1L))
                .willReturn(itemDto);

        // When
        when(itemService
                .saveComment(comment, 1L, 1L))
                .thenReturn(commentDto);

        MockHttpServletResponse response = mockMvc.perform(
                post("/items/1/comment").contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequestAttemptComment.write(comment).getJson())
                        .header("X-Sharer-User-Id", 1))
                .andReturn().getResponse();

        // Then
        then(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        then(response.getContentAsString()).isEqualTo(
                jsonResultAttemptCommentDto.write(
                        commentDto
                ).getJson()
        );
    }
}
