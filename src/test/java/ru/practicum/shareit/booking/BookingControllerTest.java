package ru.practicum.shareit.booking;

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

import ru.practicum.shareit.booking.constant.BookingStatus;
import ru.practicum.shareit.booking.controller.BookingController;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

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
@WebMvcTest(BookingController.class)
public class BookingControllerTest {

    @MockBean
    private BookingService bookingService;

    @Autowired
    MockMvc mockMvc;

    @Autowired
    private JacksonTester<BookingDto> jsonRequestAttemptBooking;

    @Autowired
    private JacksonTester<BookingDto> jsonResultAttemptBooking;

    private User itemOwner;
    private User itemBooker;
    private Item item;
    private Booking booking;

    @BeforeEach
    void setUp() {

        itemOwner = new User(
                "owner",
                "owner@mail.com"
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
                null
        );

        booking = new Booking(
                LocalDateTime.of(2023,11,25,12, 10, 00),
                LocalDateTime.of(2023, 11, 26, 12, 10, 00),
                item,
                itemBooker
        );

        item.setId(1L);
        itemOwner.setId(1L);
        itemBooker.setId(2L);

    }

    @Test
    void shouldPostUser() throws Exception {
        // Given
        BookingDto bookingDto = new BookingDto(
                1L,
                1L,
                LocalDateTime.of(2023, 11, 25, 12, 10, 00),
                LocalDateTime.of(2023, 11, 26, 12, 10, 00),
                item,
                itemBooker,
                BookingStatus.WAITING
        );

        // When
        when(bookingService
                .saveBooking(bookingDto,2L))
                .thenReturn(bookingDto);

        MockHttpServletResponse response = mockMvc.perform(
                post("/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequestAttemptBooking.write(bookingDto).getJson())
                        .header("X-Sharer-User-Id", 2L)
        ).andReturn().getResponse();

        // Then
        then(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        then(response.getContentAsString()).isEqualTo(
                jsonResultAttemptBooking.write(bookingDto).getJson()
        );

    }

    @Test
    void shouldGetBookingById() throws Exception {
        // Given
        BookingDto bookingDto = new BookingDto(
                1L,
                1L,
                LocalDateTime.of(2023, 11, 25, 12, 10, 00),
                LocalDateTime.of(2023, 11, 26, 12, 10, 00),
                item,
                itemBooker,
                BookingStatus.WAITING
        );

        given(bookingService.getBookingById(1,2)).willReturn(bookingDto);

        // When
        MockHttpServletResponse response = mockMvc.perform(
                get("/bookings/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .contentType(jsonRequestAttemptBooking.write(bookingDto).getJson())
                        .header("X-Sharer-User-Id", 2)
        ).andReturn().getResponse();

        // Then
        then(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        then(response.getContentAsString()).isEqualTo(
                jsonResultAttemptBooking.write(
                        bookingDto
                ).getJson()
        );

    }

    @Test
    void shouldPatchUpdateBookingStatusToApprovedWhenTrue() throws Exception {
        // Given
        BookingDto bookingDto = new BookingDto(
                1L,
                1L,
                LocalDateTime.of(2023, 11, 25, 12, 10, 00),
                LocalDateTime.of(2023, 11, 26, 12, 10, 00),
                item,
                itemBooker,
                BookingStatus.APPROVED
        );

        given(bookingService.updateBookingStatus(1, true, 2)).willReturn(bookingDto);

        // When
        MockHttpServletResponse response = mockMvc.perform(
                patch("/bookings/1?approved=true")
                        .contentType(MediaType.APPLICATION_JSON)
                        .contentType(jsonRequestAttemptBooking.write(bookingDto).getJson())
                        .header("X-Sharer-User-Id", 2)
        ).andReturn().getResponse();

        // Then
        then(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        then(response.getContentAsString()).isEqualTo(
                jsonResultAttemptBooking.write(bookingDto).getJson()
        );

    }

    @Test
    void shouldPatchUpdateBookingStatusToRejectedWhenFalse() throws Exception {
        // Given
        BookingDto bookingDto = new BookingDto(
                1L,
                1L,
                LocalDateTime.of(2023, 11, 25, 12, 10, 00),
                LocalDateTime.of(2023, 11, 26, 12, 10, 00),
                item,
                itemBooker,
                BookingStatus.REJECTED
        );

        given(bookingService
                .updateBookingStatus(1, false, 2))
                .willReturn(bookingDto);

        // When
        MockHttpServletResponse response = mockMvc.perform(
                patch("/bookings/1?approved=false")
                        .contentType(MediaType.APPLICATION_JSON)
                        .contentType(jsonRequestAttemptBooking.write(bookingDto).getJson())
                        .header("X-Sharer-User-Id", 2)
        ).andReturn().getResponse();

        // Then
        then(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        then(response.getContentAsString()).isEqualTo(
                jsonResultAttemptBooking.write(bookingDto).getJson()
        );

    }

    @Test
    void shouldGetAllBookingsByBookerAndStatusWaiting() throws Exception {
        // Given
        BookingDto bookingDto = new BookingDto(
                1L,
                1L,
                LocalDateTime.of(2023, 11, 25, 12, 10, 0),
                LocalDateTime.of(2023, 11, 26, 12, 10, 0),
                item,
                itemBooker,
                BookingStatus.WAITING
        );

        given(bookingService
                .getAllBookingsByBooker("WAITING", 2L, 0, 5))
                .willReturn(List.of(bookingDto));

        // When
        mockMvc.perform(
                get("/bookings?state=WAITING")
                        .accept(MediaType.APPLICATION_JSON)
                        .requestAttr("from", 0)
                        .requestAttr("size", 5)
                        .header("X-Sharer-User-Id", 2))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(bookingDto.getId()), Long.class))
                .andExpect(jsonPath("$[0].itemId", is(bookingDto.getItemId()), Long.class))
                .andExpect(jsonPath("$[0].item", is(bookingDto.getItem()), Item.class))
                .andExpect(jsonPath("$[0].booker", is(bookingDto.getBooker()), User.class));

    }

    @Test
    void shouldGetAllBookingsByBookerAndStatusRejected() throws Exception {
        // Given
        BookingDto bookingDto = new BookingDto(
                1L,
                1L,
                LocalDateTime.of(2023, 11, 25, 12, 10, 0),
                LocalDateTime.of(2023, 11, 26, 12, 10, 0),
                item,
                itemBooker,
                BookingStatus.REJECTED
        );

        given(bookingService
                .getAllBookingsByBooker("REJECTED", 2L, 0, 5))
                .willReturn(List.of(bookingDto));

        // When
        mockMvc.perform(
                get("/bookings?state=REJECTED")
                        .accept(MediaType.APPLICATION_JSON)
                        .requestAttr("from", 0)
                        .requestAttr("size", 5)
                        .header("X-Sharer-User-Id", 2))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(bookingDto.getId()), Long.class))
                .andExpect(jsonPath("$[0].itemId", is(bookingDto.getItemId()), Long.class))
                .andExpect(jsonPath("$[0].item", is(bookingDto.getItem()), Item.class))
                .andExpect(jsonPath("$[0].booker", is(bookingDto.getBooker()), User.class));

    }

    @Test
    void shouldGetAllBookingsByBookerAndStatusCurrent() throws Exception {
        // Given
        BookingDto bookingDto = new BookingDto(
                1L,
                1L,
                LocalDateTime.of(2023, 11, 24, 12, 10, 0),
                LocalDateTime.of(2023, 11, 29, 12, 10, 0),
                item,
                itemBooker,
                BookingStatus.WAITING
        );

        given(bookingService
                .getAllBookingsByBooker("CURRENT", 2L, 0, 5))
                .willReturn(List.of(bookingDto));

        // When
        mockMvc.perform(
                get("/bookings?state=CURRENT")
                        .accept(MediaType.APPLICATION_JSON)
                        .requestAttr("from", 0)
                        .requestAttr("size", 5)
                        .header("X-Sharer-User-Id", 2))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(bookingDto.getId()), Long.class))
                .andExpect(jsonPath("$[0].itemId", is(bookingDto.getItemId()), Long.class))
                .andExpect(jsonPath("$[0].item", is(bookingDto.getItem()), Item.class))
                .andExpect(jsonPath("$[0].booker", is(bookingDto.getBooker()), User.class));

    }

    @Test
    void shouldGetAllBookingsByBookerAndStatusPast() throws Exception {
        // Given
        BookingDto bookingDto = new BookingDto(
                1L,
                1L,
                LocalDateTime.of(2023, 11, 20, 12, 10, 0),
                LocalDateTime.of(2023, 11, 21, 12, 10, 0),
                item,
                itemBooker,
                BookingStatus.WAITING
        );

        given(bookingService
                .getAllBookingsByBooker("PAST", 2, 0, 5))
                .willReturn(List.of(bookingDto));

        // When
        mockMvc.perform(
                get("/bookings?state=PAST")
                        .accept(MediaType.APPLICATION_JSON)
                        .requestAttr("from", 0)
                        .requestAttr("size", 5)
                        .header("X-Sharer-User-Id", 2))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(bookingDto.getId()), Long.class))
                .andExpect(jsonPath("$[0].itemId", is(bookingDto.getItemId()), Long.class))
                .andExpect(jsonPath("$[0].item", is(bookingDto.getItem()), Item.class))
                .andExpect(jsonPath("$[0].booker", is(bookingDto.getBooker()), User.class));

    }

    @Test
    void shouldGetAllBookingsByItemOwnerAndStatusWaiting() throws Exception {
        // Given
        BookingDto bookingDto = new BookingDto(
                1L,
                1L,
                LocalDateTime.of(2023, 11, 24, 12, 10, 0),
                LocalDateTime.of(2023, 11, 29, 12, 10, 0),
                item,
                itemBooker,
                BookingStatus.WAITING
        );

        given(bookingService
                .getAllBookingsByItemOwner("WAITING", 1, 0, 5))
                .willReturn(List.of(bookingDto));

        // When
        mockMvc.perform(
                get("/bookings/owner?state=WAITING")
                        .accept(MediaType.APPLICATION_JSON)
                        .requestAttr("from", 0)
                        .requestAttr("size", 5)
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(bookingDto.getId()), Long.class))
                .andExpect(jsonPath("$[0].itemId", is(bookingDto.getItemId()), Long.class))
                .andExpect(jsonPath("$[0].item", is(bookingDto.getItem()), Item.class))
                .andExpect(jsonPath("$[0].booker", is(bookingDto.getBooker()), User.class));

    }

    @Test
    void shouldGetAllBookingsByItemOwnerAndStatusRejected() throws Exception {
        // Given
        BookingDto bookingDto = new BookingDto(
                1L,
                1L,
                LocalDateTime.of(2023, 11, 24, 12, 10, 0),
                LocalDateTime.of(2023, 11, 29, 12, 10, 0),
                item,
                itemBooker,
                BookingStatus.REJECTED
        );

        given(bookingService
                .getAllBookingsByItemOwner("REJECTED", 1, 0, 5))
                .willReturn(List.of(bookingDto));

        // When
        mockMvc.perform(
                get("/bookings/owner?state=REJECTED")
                        .accept(MediaType.APPLICATION_JSON)
                        .requestAttr("from", 0)
                        .requestAttr("size", 5)
                        .header("X-Sharer-User-id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(bookingDto.getId()), Long.class))
                .andExpect(jsonPath("$[0].itemId", is(bookingDto.getItemId()), Long.class))
                .andExpect(jsonPath("$[0].item", is(bookingDto.getItem()), Item.class))
                .andExpect(jsonPath("$[0].booker", is(bookingDto.getBooker()), User.class));

    }

    @Test
    void shouldGetAllBookingsByItemOwnerAndStatusCurrent() throws Exception {
        // Given
        BookingDto bookingDto = new BookingDto(
                1L,
                1L,
                LocalDateTime.of(2023, 11, 24, 12, 10, 0),
                LocalDateTime.of(2023, 11, 29, 12, 10, 0),
                item,
                itemBooker,
                BookingStatus.WAITING
        );

        given(bookingService
                .getAllBookingsByItemOwner("CURRENT", 1, 0, 5))
                .willReturn(List.of(bookingDto));

        // When
        mockMvc.perform(
                get("/bookings/owner?state=CURRENT")
                        .accept(MediaType.APPLICATION_JSON)
                        .requestAttr("from", 0)
                        .requestAttr("size", 5)
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(bookingDto.getId()), Long.class))
                .andExpect(jsonPath("$[0].itemId", is(bookingDto.getItemId()), Long.class))
                .andExpect(jsonPath("$[0].item", is(bookingDto.getItem()), Item.class))
                .andExpect(jsonPath("$[0].booker", is(bookingDto.getBooker()), User.class));

    }

    @Test
    void shouldGetAllBookingsByItemOwnerAndStatusPast() throws Exception {
        // Given
        BookingDto bookingDto = new BookingDto(
                1L,
                1L,
                LocalDateTime.of(2023, 11, 20, 12, 10, 0),
                LocalDateTime.of(2023, 11, 21, 12, 10, 0),
                item,
                itemBooker,
                BookingStatus.WAITING
        );

        given(bookingService
                .getAllBookingsByItemOwner("PAST", 1, 0, 5))
                .willReturn(List.of(bookingDto));

        // When
        mockMvc.perform(
                get("/bookings/owner?state=PAST")
                        .accept(MediaType.APPLICATION_JSON)
                        .requestAttr("from", 0)
                        .requestAttr("size", 5)
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(bookingDto.getId()), Long.class))
                .andExpect(jsonPath("$[0].itemId", is(bookingDto.getItemId()), Long.class))
                .andExpect(jsonPath("$[0].item", is(bookingDto.getItem()), Item.class))
                .andExpect(jsonPath("$[0].booker", is(bookingDto.getBooker()), User.class));

    }

}
