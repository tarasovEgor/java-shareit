package ru.practicum.shareit.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemWithBookingDto;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.service.impl.ItemServiceImpl;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.validation.ItemValidation;

import java.time.*;
import java.util.List;
import java.util.Optional;

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
    void shouldNotSaveItemWhenItemNameIsEmpty() {
        // Given
        Item invalidItem = new Item(
                "",
                "desc",
                false,
                user,
                2L
        );

        // Then
        assertThrows(InvalidItemNameException.class,
                () -> itemService.saveItem(invalidItem, 1));

    }

    @Test
    void shouldNotSaveItemWhenItemDescriptionIsNull() {
        // Given
        Item invalidItem = new Item(
                "item",
                null,
                false,
                user,
                2L
        );

        // Then
        assertThrows(InvalidItemDescriptionException.class,
                () -> itemService.saveItem(invalidItem, 1));

    }

    @Test
    void shouldNotSaveItemWhenItemAvailableFieldIsNull() {
        // Given
        Item invalidItem = new Item(
                "item",
                "desc",
                null,
                user,
                2L
        );

        // Then
        assertThrows(InvalidAvailableFieldException.class,
                () -> itemService.saveItem(invalidItem, 1));
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
    void shouldUpdateItemName() {
        // Given
        given(itemRepository.save(item)).willReturn(item);

        // When
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(itemRepository.save(any())).thenReturn(item);

        ItemDto savedItem = itemService.saveItem(item, 1L);

        item.setName("updated");

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

    @Test
    void shouldNotSaveCommentWithNoText() {
        // Given
        Comment comment = new Comment(
                1L,
                "",
                item,
                user,
                LocalDateTime.of(23, 11, 30, 12, 12)
        );

        // Then
        assertThrows(InvalidCommentException.class, () -> itemService.saveComment(comment, 1L, 1L));

    }

    @Test
    void shouldNotSaveCommentWithNoBookingByAuthor() {
        // Given
        User author = new User(
                "author",
                "author@mail.com"
        );

        Comment comment = new Comment(
                1L,
                "text",
                item,
                author,
                LocalDateTime.of(2023, 11, 30, 11, 30)
        );

        Booking booking = new Booking(
                LocalDateTime.of(2023, 10, 24, 12, 20),
                LocalDateTime.of(LocalDate.of(2023, 11, 20), LocalTime.of(10, 23)),
                item,
                author
        );

        author.setId(1L);

        given(itemRepository.findById(anyLong())).willReturn(Optional.of(item));
        given(userRepository.findById(anyLong())).willReturn(Optional.of(author));

        // When
        when(bookingRepository
                .findBookingsByItemAndOwnerOrderByStartDesc(
                        1L, author, LocalDateTime.of(
                                2023, 11, 30, 12, 12)
                )).thenReturn(List.of());

        // Then
        assertThrows(NoBookingForCommentException.class, () -> itemService.saveComment(comment, 1, 1));

    }

    @Test
    void shouldMapItemsToItemWithBookingDtoBookingsAreNull() {
        // Given
        User itemOwner1 = new User("user1", "user1@mail.com");
       // User itemOwner2 = new User("user2", "user2@mail.com");
        User author = new User("author", "author@mail.com");

        itemOwner1.setId(1L);
       // itemOwner2.setId(2L);
        author.setId(3L);

        Item item1 = new Item(
                "item1",
                "desc",
                true,
                itemOwner1,
                1L
        );

//        Item item2 = new Item(
//                "item2",
//                "desc",
//                true,
//                itemOwner2,
//                2L
//        );

        Booking nextBooking = new Booking(
                LocalDateTime.of(2023, 12, 5, 12, 12),
                LocalDateTime.of(2023, 12, 7, 12, 12),
                item1,
                itemOwner1
        );

        Booking lastBooking = new Booking(
                LocalDateTime.of(2023, 11, 5, 12, 12),
                LocalDateTime.of(2023, 11, 7, 12, 12),
                item1,
                itemOwner1
        );

        Comment comment = new Comment(
                1L,
                "text",
                item1,
                author,
                LocalDateTime.of(2023, 11, 12, 12, 12)
        );

        CommentDto commentDto = CommentMapper.toCommentDto(comment, item1, "author");
//
//        userRepository.save(itemOwner1);
//        userRepository.save(author);
//
//        itemRepository.save(item1);
//
//        bookingRepository.save(nextBooking);
//        bookingRepository.save(lastBooking);

        List<Item> items = List.of(item1);
        List<Booking> bookings = List.of(nextBooking, lastBooking);
        List<CommentDto> comments = List.of(commentDto);

//        when(userRepository.findAll()).thenReturn(List.of(itemOwner1, author));
//        when(itemRepository.findAll()).thenReturn(List.of(item1));
//        when(bookingRepository.findAll()).thenReturn(List.of(nextBooking, lastBooking));

        given(itemRepository.findById(item1.getId())).willReturn(Optional.of(item1));

        List<ItemWithBookingDto> itemWithBookingDtos = ItemMapper.toItemDtoWithBookings(
                items,
                bookings,
                itemOwner1,
                itemRepository,
                bookingRepository,
                comments
        );

        System.out.println(itemWithBookingDtos);
        assertNotNull(itemWithBookingDtos);
        assertThat(itemWithBookingDtos.get(0).getName()).isEqualTo(item1.getName());
        assertThat(itemWithBookingDtos.get(0).getDescription()).isEqualTo(item1.getDescription());
        assertThat(itemWithBookingDtos.get(0).getRequest()).isEqualTo(item1.getRequestId());
        assertThat(itemWithBookingDtos.get(0).getNextBooking()).isNull();
        assertThat(itemWithBookingDtos.get(0).getLastBooking()).isNull();
        assertThat(itemWithBookingDtos.get(0).getComments().get(0)).isEqualTo(commentDto);

    }

    @Test
    void shouldMapItemsToItemWithBookingDtoBookingsAreNullDifferentItemId() {
        // Given
        User itemOwner1 = new User("user1", "user1@mail.com");
        User itemOwner2 = new User("user2", "user2@mail.com");
        User author = new User("author", "author@mail.com");

        itemOwner1.setId(1L);
        itemOwner2.setId(2L);
        author.setId(3L);

        Item item1 = new Item(
                "item1",
                "desc",
                true,
                itemOwner1,
                1L
        );

        item1.setId(1L);

        Item item2 = new Item(
                "item2",
                "desc",
                true,
                itemOwner2,
                2L
        );

        item2.setId(2L);

        Booking nextBooking = new Booking(
                LocalDateTime.of(2023, 12, 9, 12, 12),
                LocalDateTime.of(2023, 12, 17, 12, 12),
                item1,
                itemOwner1
        );

        Booking lastBooking = new Booking(
                LocalDateTime.of(2023, 11, 5, 12, 12),
                LocalDateTime.of(2023, 11, 7, 12, 12),
                item1,
                itemOwner1
        );

        Comment comment = new Comment(
                1L,
                "text",
                item1,
                author,
                LocalDateTime.of(2023, 11, 12, 12, 12)
        );

        CommentDto commentDto = CommentMapper.toCommentDto(comment, item1, "author");

        List<Item> items = List.of(item2);
        List<Booking> bookings = List.of(nextBooking, lastBooking);
        List<CommentDto> comments = List.of(commentDto);

        given(itemRepository.findById(item2.getId())).willReturn(Optional.of(item2));

        List<ItemWithBookingDto> itemWithBookingDtos = ItemMapper.toItemDtoWithBookings(
                items,
                bookings,
                itemOwner1,
                itemRepository,
                bookingRepository,
                comments
        );

        assertNotNull(itemWithBookingDtos);
        assertThat(itemWithBookingDtos.get(0).getId()).isEqualTo(2L);
        assertThat(itemWithBookingDtos.get(0).getName()).isEqualTo(item2.getName());
        assertThat(itemWithBookingDtos.get(0).getDescription()).isEqualTo(item2.getDescription());
        assertThat(itemWithBookingDtos.get(0).getRequest()).isEqualTo(item2.getRequestId());
        assertThat(itemWithBookingDtos.get(0).getNextBooking()).isNull();
        assertThat(itemWithBookingDtos.get(0).getLastBooking()).isNull();
        assertThat(itemWithBookingDtos.get(0).getComments().get(0)).isEqualTo(commentDto);

    }

    @Test
    void shouldUpdateItemNameItemValidation() {
        // Given
        Item itemWithNewName = new Item(
                "updated",
                null,
                null,
                user,
                1L
        );

        item.setId(1L);
        itemWithNewName.setId(1L);

        given(itemRepository
                .findById(anyLong()))
                .willReturn(Optional.of(item));

        // When
        ItemDto itemDto = ItemValidation
                .isItemValidForUpdate(itemWithNewName, 1L, user, itemRepository);

        // Then
        assertNotNull(itemDto);

    }

    @Test
    void shouldUpdateItemDescriptionItemValidation() {
        // Given
        Item itemWithDescription = new Item(
                null,
                "desc",
                null,
                user,
                1L
        );

        item.setId(1L);
        itemWithDescription.setId(1L);

        given(itemRepository
                .findById(anyLong()))
                .willReturn(Optional.of(item));

        // When
        ItemDto itemDto = ItemValidation
                .isItemValidForUpdate(itemWithDescription, 1L, user, itemRepository);

        assertNotNull(itemDto);

    }

    @Test
    void shouldUpdateItemAvailableItemValidation() {
        // Given
        Item itemWithAvailable = new Item(
                null,
                null,
                false,
                user,
                1L
        );

        item.setId(1L);
        itemWithAvailable.setId(1L);

        given(itemRepository
                .findById(anyLong()))
                .willReturn(Optional.of(item));

        // When
        ItemDto itemDto = ItemValidation
                .isItemValidForUpdate(itemWithAvailable, 1L, user, itemRepository);

        assertNotNull(itemDto);

    }
//    @Test
//    void shouldSaveComment() {
//        // Given
//        Item newItem = new Item(
//                "item",
//                "desc",
//                true,
//                user,
//                1L
//        );
//
//        newItem.setId(1L);
//
//        User author = new User(
//                "author",
//                "author@mail.com"
//        );
//
//        Comment comment = new Comment(
//                1L,
//                "text",
//                newItem,
//                author,
//                LocalDateTime.of(2023, 11, 30, 11, 30)
//        );
//
//
//
//        Booking booking = new Booking(
//                LocalDateTime.of(2023, 10, 24, 12, 20),
//                LocalDateTime.of(LocalDate.of(2023, 11, 20), LocalTime.of(10, 23)),
//                newItem,
//                author
//        );
//
//        author.setId(1L);
//
//        given(itemRepository.findById(anyLong())).willReturn(Optional.of(newItem));
//        given(userRepository.findById(anyLong())).willReturn(Optional.of(author));
//
//        given(bookingRepository.findById(anyLong())).willReturn(Optional.of(booking));
//
//        when(bookingRepository
//                .findBookingsByItemAndOwnerOrderByStartDesc(
//                        1L, author, LocalDateTime.of(
//                                anyInt(), anyInt(), 30, anyInt(), anyInt(), anyInt(), anyInt())
//                )).thenReturn(List.of(booking));
//
//        // When
//        when(commentRepository.save(comment)).thenReturn(comment);
//
//        CommentDto commentDto = itemService.saveComment(comment, 1L, 1L);
//
//
//    }

    @Test
    void shouldMapItemWithRequestIdToItemDto() {
        // Given
        Item itemWithRequestId = new Item(
                "item",
                "desc",
                true,
                user,
                2L
        );

        // When
        ItemDto itemDto = ItemMapper.toItemDto(itemWithRequestId);

        // Then
        assertNotNull(itemDto);
        assertThat(itemDto.getName()).isEqualTo(itemWithRequestId.getName());
        assertThat(itemDto.getDescription()).isEqualTo(itemWithRequestId.getDescription());
        assertThat(itemDto.getAvailable()).isTrue();
        assertThat(itemDto.getRequestId()).isEqualTo(2L);
    }


    @Test
    void shouldMapListOfCommentsToAListOfCommentDtos() {
        User user1 = new User("user1", "user1@mail.com");
        User user2 = new User("user2", "user2@mail.com");


        Comment comment1 = new Comment(
                1L,
                "text",
                item,
                user1,
                LocalDateTime.of(2023, 12, 12, 12, 12)
        );

        Comment comment2 = new Comment(
                2L,
                "text",
                item,
                user2,
                LocalDateTime.of(2023, 11, 12, 12, 12)
        );

        List<Comment> comments = List.of(comment1, comment2);

        CommentDto commentDto1 = CommentMapper.toCommentDto(comment1, item, "user1");
        CommentDto commentDto2 = CommentMapper.toCommentDto(comment2, item, "user2");


        List<CommentDto> commentDtos = CommentMapper.toCommentDto(comments);

        assertNotNull(commentDtos);
        assertThat(commentDtos.contains(commentDto1));
        assertThat(commentDtos.contains(commentDto2));

    }
}
