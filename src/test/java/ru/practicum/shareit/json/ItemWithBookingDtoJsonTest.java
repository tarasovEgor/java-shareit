package ru.practicum.shareit.json;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.booking.constant.BookingStatus;
import ru.practicum.shareit.booking.dto.BookingWithBookerIdDto;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemWithBookingDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class ItemWithBookingDtoJsonTest {

    @Autowired
    private JacksonTester<ItemWithBookingDto> json;

    private User itemOwner;
    private User itemBooker1;
    private User itemBooker2;
    private Item item;
    private BookingWithBookerIdDto nextBooking;
    private BookingWithBookerIdDto lastBooking;
    private CommentDto commentDto;

    @BeforeEach
    void setUp() {

        itemOwner = new User(
                "user",
                "user@mail.com"
        );

        itemBooker1 = new User(
                "booker1",
                "booker1@mail.com"
        );

        itemBooker2 = new User(
                "booker2",
                "booker2@mail.com"
        );

        item = new Item(
                "item",
                "desc",
                true,
                itemOwner,
                1L
        );

        nextBooking = new BookingWithBookerIdDto(
                1L,
                LocalDateTime.of(2023, 12, 12, 12, 30, 0),
                LocalDateTime.of(2023, 12, 15, 12, 30, 0),
                item,
                2L,
                BookingStatus.REJECTED
        );

        lastBooking = new BookingWithBookerIdDto(
                2L,
                LocalDateTime.of(2023, 11, 12, 12, 30, 0),
                LocalDateTime.of(2023, 11, 15, 12, 30, 0),
                item,
                3L,
                BookingStatus.APPROVED
        );

        commentDto = new CommentDto(
                1L,
                "text",
                item,
                "author"
        );

        item.setId(1L);
        itemOwner.setId(1L);
        itemBooker1.setId(2L);
        itemBooker2.setId(3L);
    }

    @Test
    void testItemWithBookingDto() throws Exception {
        // Given
        ItemWithBookingDto itemWithBookingDto = new ItemWithBookingDto(
                1L,
                "itemWithBooking",
                "description",
                true,
                1L,
                nextBooking,
                lastBooking,
                List.of(commentDto)
        );

        // When
        JsonContent<ItemWithBookingDto> result = json.write(itemWithBookingDto);

        // Then
        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathNumberValue("$.request").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.name").isEqualTo("itemWithBooking");
        assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo("description");
        assertThat(result).extractingJsonPathValue("$.available").isEqualTo(true);

        assertThat(result).extractingJsonPathValue("$.nextBooking.id").isEqualTo(1);
        assertThat(result).extractingJsonPathValue("$.nextBooking.start").isEqualTo("2023-12-12T12:30:00");
        assertThat(result).extractingJsonPathValue("$.nextBooking.end").isEqualTo("2023-12-15T12:30:00");
        assertThat(result).extractingJsonPathValue("$.nextBooking.item.id").isEqualTo(1);
        assertThat(result).extractingJsonPathValue("$.nextBooking.item.name").isEqualTo("item");
        assertThat(result).extractingJsonPathValue("$.nextBooking.item.description").isEqualTo("desc");
        assertThat(result).extractingJsonPathValue("$.nextBooking.item.available").isEqualTo(true);
        assertThat(result).extractingJsonPathValue("$.nextBooking.item.owner.id").isEqualTo(1);
        assertThat(result).extractingJsonPathValue("$.nextBooking.item.owner.name").isEqualTo("user");
        assertThat(result).extractingJsonPathValue("$.nextBooking.item.owner.email").isEqualTo("user@mail.com");
        assertThat(result).extractingJsonPathValue("$.nextBooking.item.requestId").isEqualTo(1);
        assertThat(result).extractingJsonPathValue("$.nextBooking.bookerId").isEqualTo(2);
        assertThat(result).extractingJsonPathValue("$.nextBooking.status").isEqualTo("REJECTED");

        assertThat(result).extractingJsonPathValue("$.lastBooking.id").isEqualTo(2);
        assertThat(result).extractingJsonPathValue("$.lastBooking.start").isEqualTo("2023-11-12T12:30:00");
        assertThat(result).extractingJsonPathValue("$.lastBooking.end").isEqualTo("2023-11-15T12:30:00");
        assertThat(result).extractingJsonPathValue("$.lastBooking.item.id").isEqualTo(1);
        assertThat(result).extractingJsonPathValue("$.lastBooking.item.name").isEqualTo("item");
        assertThat(result).extractingJsonPathValue("$.lastBooking.item.description").isEqualTo("desc");
        assertThat(result).extractingJsonPathValue("$.lastBooking.item.available").isEqualTo(true);
        assertThat(result).extractingJsonPathValue("$.lastBooking.item.owner.id").isEqualTo(1);
        assertThat(result).extractingJsonPathValue("$.lastBooking.item.owner.name").isEqualTo("user");
        assertThat(result).extractingJsonPathValue("$.lastBooking.item.owner.email").isEqualTo("user@mail.com");
        assertThat(result).extractingJsonPathValue("$.lastBooking.item.requestId").isEqualTo(1);
        assertThat(result).extractingJsonPathValue("$.lastBooking.bookerId").isEqualTo(3);
        assertThat(result).extractingJsonPathValue("$.lastBooking.status").isEqualTo("APPROVED");

        assertThat(result).extractingJsonPathValue("$.comments[0].id").isEqualTo(1);
        assertThat(result).extractingJsonPathValue("$.comments[0].text").isEqualTo("text");
        assertThat(result).extractingJsonPathValue("$.comments[0].item.id").isEqualTo(1);
        assertThat(result).extractingJsonPathValue("$.comments[0].item.name").isEqualTo("item");
        assertThat(result).extractingJsonPathValue("$.comments[0].item.description").isEqualTo("desc");
        assertThat(result).extractingJsonPathValue("$.comments[0].item.available").isEqualTo(true);
        assertThat(result).extractingJsonPathValue("$.comments[0].item.owner.id").isEqualTo(1);
        assertThat(result).extractingJsonPathValue("$.comments[0].item.owner.name").isEqualTo("user");
        assertThat(result).extractingJsonPathValue("$.comments[0].item.owner.email").isEqualTo("user@mail.com");
        assertThat(result).extractingJsonPathValue("$.comments[0].item.requestId").isEqualTo(1);
        assertThat(result).extractingJsonPathValue("$.comments[0].authorName").isEqualTo("author");

    }

}
