package ru.practicum.shareit.json;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import ru.practicum.shareit.booking.constant.BookingStatus;
import ru.practicum.shareit.booking.dto.BookingWithBookerIdDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class BookingWithBookerIdDtoJsonTest {

    @Autowired
    private JacksonTester<BookingWithBookerIdDto> json;

    private User itemOwner;
    private User itemBooker;
    private Item item;

    @BeforeEach
    void setUp() {

        itemBooker = new User(
                "booker",
                "booker@mail.com"
        );

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
        itemBooker.setId(2L);
        item.setId(1L);

    }

    @Test
    void testBookingWithBookerIdDto() throws Exception {
        // Given
        BookingWithBookerIdDto bookingWithBookerIdDto = new BookingWithBookerIdDto(
                1L,
                LocalDateTime.of(2023, 12, 11, 10, 10, 0),
                LocalDateTime.of(2023, 12, 15, 10, 10, 0),
                item,
                itemBooker.getId(),
                BookingStatus.WAITING
        );

        // When
        JsonContent<BookingWithBookerIdDto> result = json.write(bookingWithBookerIdDto);

        // Then
        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathValue("$.start").isEqualTo("2023-12-11T10:10:00");
        assertThat(result).extractingJsonPathValue("$.end").isEqualTo("2023-12-15T10:10:00");

        assertThat(result).extractingJsonPathValue("$.item.id").isEqualTo(1);
        assertThat(result).extractingJsonPathValue("$.item.name").isEqualTo("item");
        assertThat(result).extractingJsonPathValue("$.item.description").isEqualTo("desc");
        assertThat(result).extractingJsonPathValue("$.item.available").isEqualTo(true);
        assertThat(result).extractingJsonPathValue("$.item.owner.id").isEqualTo(1);
        assertThat(result).extractingJsonPathValue("$.item.owner.name").isEqualTo("user");
        assertThat(result).extractingJsonPathValue("$.item.owner.email").isEqualTo("user@mail.com");

        assertThat(result).extractingJsonPathValue("$.bookerId").isEqualTo(2);
        assertThat(result).extractingJsonPathValue("$.status").isEqualTo("WAITING");

    }


}
