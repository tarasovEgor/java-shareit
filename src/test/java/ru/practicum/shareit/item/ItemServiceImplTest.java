package ru.practicum.shareit.item;

import org.checkerframework.checker.nullness.Opt;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import ru.practicum.shareit.booking.dto.BookingWithBookerIdDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.ItemDoesNotExistException;
import ru.practicum.shareit.exception.NoBookingForCommentException;
import ru.practicum.shareit.exception.OwnerDoesNotExistException;
import ru.practicum.shareit.exception.UserDoesNotExistException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemWithBookingDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.item.service.impl.ItemServiceImpl;
import ru.practicum.shareit.user.dao.UserDao;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.user.service.impl.UserServiceImpl;

import javax.persistence.Entity;
import javax.persistence.EntityManager;

import java.awt.print.Book;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class ItemServiceImplTest {

//    @Mock
//    ItemService itemService;
//
//    @Mock
//    UserService userService;
//
//    @Mock
//    ItemRepository itemRepository;
//
//    @Mock
//    UserRepository userRepository;

    @Mock
    ItemRepository itemRepository;

    @Mock
    CommentRepository commentRepository;

    @Mock
    BookingRepository bookingRepository;

    @Mock
    UserRepository userRepository;

    @InjectMocks
    ItemServiceImpl itemService;

    private Item item;
//    private ItemDto itemDto;
    private User user;

    @BeforeEach
    public void setup() {

        user = new User(
            "user",
            "user@mail.com"
        );

        user.setId(1L);

        item = new Item(
                "item",
                "description",
                true,
                null,
                null
        );

        item.setId(1L);
//
//        itemDto = new ItemDto(
//                1L,
//                "item",
//                "description",
//                false,
//                null
//        );


    }

    @Test
    void shouldSaveItem() {
//        User user = new User("user", "user@mail.com");
//        Item item = new Item("item", "desc", true, user, null);

        ItemDto itemDto = ItemMapper.toItemDto(item);

        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(itemRepository.save(any())).thenReturn(item);

        ItemDto savedItem = itemService.saveItem(item, 1L);

        assertNotNull(savedItem);

        assertThat(savedItem.getId()).isEqualTo(itemDto.getId());
        assertThat(savedItem.getName()).isEqualTo(itemDto.getName());
        assertThat(savedItem.getDescription()).isEqualTo(itemDto.getDescription());
        assertThat(savedItem.getAvailable()).isEqualTo(itemDto.getAvailable());

        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(UserDoesNotExistException.class, () -> itemService.saveItem(item, 1));
    }

    @Test
    void shouldGetItemById() {
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

        given(itemRepository.findById(1L)).willReturn(Optional.of(item));

        // When
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));

        ItemWithBookingDto returnedItem = itemService.getItemById(item.getId(), 1L);

        // Then
        assertThat(returnedItem).isNotNull();
        assertThat(returnedItem.getId()).isEqualTo(itemWithBookingDto.getId());
        assertThat(returnedItem.getName()).isEqualTo(itemWithBookingDto.getName());
        assertThat(returnedItem.getDescription()).isEqualTo(itemWithBookingDto.getDescription());
    }

    @Test
    void shouldNotGetItemByNonExistingId() {
        // When
        when(itemRepository.findById(anyLong())).thenReturn(Optional.empty());

        // Given
        assertThrows(ItemDoesNotExistException.class, () ->
                itemService.getItemById(item.getId(), 1));
    }

    @Test
    void shouldGetAllItems() {
        // Given
        Item additionalItem = new Item(
                "additional",
                "desc",
                false,
                user,
                null
        );

        given(itemRepository.findAll()).willReturn(List.of(item, additionalItem));

        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));

        List<ItemWithBookingDto> items = itemService.getAllItems(1L);

        assertNotNull(items);
    }

    @Test
    void shouldUpdateItem() {
        // Given
        given(itemRepository.save(item)).willReturn(item);

        // When
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(itemRepository.save(any())).thenReturn(item);

        ItemDto savedItem = itemService.saveItem(item, 1L);

        item.setName("updated");
        item.setDescription("updated");
        item.setAvailable(false);

        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(ItemMapper.toItem(savedItem, user)));

        ItemDto updatedItem = itemService.updateItem(item, 1L, 1L);

        // Then
        assertThat(updatedItem).isNotNull();
        assertThat(updatedItem.getId()).isEqualTo(item.getId());
        assertThat(updatedItem.getName()).isEqualTo(item.getName());
        assertThat(updatedItem.getDescription()).isEqualTo(item.getDescription());
        assertThat(updatedItem.getAvailable()).isEqualTo(item.getAvailable());
    }

    @Test
    void shouldSearchItems() {
        // Given
        Item searchedItem1 = new Item(
                "search",
                "searching",
                true,
                user,
                null
        );

        Item searchedItem2 = new Item(
                "sears",
                "garden sears",
                false,
                user,
                null
        );

        Item searchedItem3 = new Item(
                "glass",
                "a kitchen glass",
                false,
                user,
                null
        );

        // When
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));

        when(itemRepository.save(any())).thenReturn(searchedItem1);
        when(itemRepository.save(any())).thenReturn(searchedItem2);
        when(itemRepository.save(any())).thenReturn(searchedItem3);

        when(itemRepository.search(any())).thenReturn(List.of(searchedItem1, searchedItem2));

        List<ItemDto> foundItems = itemService.searchItem("sear");

        assertEquals(foundItems.size(), 2);
        assertThat(foundItems.get(0).getName().contains("sear"));
        assertThat(foundItems.get(1).getName().contains("sear"));
        assertThat(foundItems.get(0).getDescription().contains("sear"));
        assertThat(foundItems.get(1).getDescription().contains("sear"));
        assertThat(foundItems.contains(searchedItem1));
        assertThat(foundItems.contains(searchedItem2));
        assertThat(!foundItems.contains(searchedItem3));
    }

//    @Test
//    void shouldSaveComment() {
//        // Given
//        Item newItem = new Item("item", "desc", false, user, null);
//        newItem.setId(1L);
//
//        Comment comment = new Comment(1L, "text", newItem, user, LocalDateTime.now());
//
//        CommentDto commentDto = new CommentDto(1L, "text", newItem, user.getName());
//
//        Booking booking = new Booking(
//                LocalDateTime.of(2023, 3, 20, 23, 30, 23),
//                LocalDateTime.of(2023, 4, 20, 23, 30, 23),
//                newItem,
//                user
//        );
//
//        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
//        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
//        when(bookingRepository.findBookingsByItemAndOwnerOrderByStartDesc(
//                1L, user, LocalDateTime.now())).thenReturn(List.of(booking));
//
////        when(itemService.saveComment(comment, any(), user.getId())).thenReturn(commentDto);
////
//        CommentDto savedComment = itemService.saveComment(comment, 1L, anyLong());
//
//        System.out.println(savedComment);
//
//    }
}
