package ru.practicum.shareit.json;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import ru.practicum.shareit.booking.constant.BookingStatus;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@Disabled
@JsonTest
public class BookingDtoJsonTest {

    @Autowired
    private JacksonTester<BookingDto> json;

    private Item item;
    private User itemOwner;
    private User itemBooker;

    @BeforeEach
    void setUp() {

        itemOwner = new User(
                "user",
                "user@mail.com"
        );

        itemBooker = new User(
                "booker",
                "booker@mail.com"
        );

        item = new Item(
                "item",
                "desc",
                true,
                itemOwner,
                1L
        );

        item.setId(1L);
        itemOwner.setId(1L);
        itemBooker.setId(2L);
    }

    @Test
    void testBookingDto() throws Exception {
        // Given
        BookingDto bookingDto = new BookingDto(
                1L,
                1L,
                LocalDateTime.of(2023, 11, 12, 12, 10, 0),
                LocalDateTime.of(2023, 11, 19, 12, 10, 0),
                item,
                itemBooker,
                BookingStatus.WAITING
        );

        // When
        JsonContent<BookingDto> result = json.write(bookingDto);

        // Then
        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathNumberValue("$.itemId").isEqualTo(1);
        assertThat(result).extractingJsonPathValue("$.item.id").isEqualTo(1);
        assertThat(result).extractingJsonPathValue("$.item.name").isEqualTo("item");
        assertThat(result).extractingJsonPathValue("$.item.available").isEqualTo(true);
        assertThat(result).extractingJsonPathValue("$.item.owner.id").isEqualTo(1);
        assertThat(result).extractingJsonPathValue("$.item.owner.name").isEqualTo("user");
        assertThat(result).extractingJsonPathValue("$.item.owner.email").isEqualTo("user@mail.com");
        assertThat(result).extractingJsonPathValue("$.booker.id").isEqualTo(2);
        assertThat(result).extractingJsonPathValue("$.booker.name").isEqualTo("booker");
        assertThat(result).extractingJsonPathValue("$.booker.email").isEqualTo("booker@mail.com");
        assertThat(result).extractingJsonPathValue("$.start").isEqualTo("2023-11-12T12:10:00");
        assertThat(result).extractingJsonPathValue("$.end").isEqualTo("2023-11-19T12:10:00");
        assertThat(result).extractingJsonPathValue("$.status").isEqualTo("WAITING");

    }

}
