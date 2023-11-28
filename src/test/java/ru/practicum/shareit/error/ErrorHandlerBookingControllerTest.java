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
import ru.practicum.shareit.booking.constant.BookingStatus;
import ru.practicum.shareit.booking.controller.BookingController;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.exception.*;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;


import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@AutoConfigureJsonTesters
@WebMvcTest(ErrorHandler.class)
public class ErrorHandlerBookingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Mock
    private BookingController bookingController;

//    @Mock
//    private BookingService bookingService;

    @Autowired
    private JacksonTester<BookingDto> jsonRequestAttemptBookingDto;

    private User itemOwner;
    private User booker;
    private Item item;
    private Booking booking;

    @BeforeEach
    void setUp() {

        this.mockMvc = MockMvcBuilders
                .standaloneSetup(new ErrorHandler(), bookingController)
                .build();

        itemOwner = new User(
                "user",
                "user@mail.com"
        );


        booker = new User(
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

        itemOwner.setId(1L);
        booker.setId(2L);
        item.setId(1L);

    }

    @Test
    void checkInvalidItemOwnerExceptionsAreCaughtAndStatus404() throws Exception {
        // Given
        Booking booking = new Booking(
                LocalDateTime.of(2023, 11, 25, 12, 10, 00),
                LocalDateTime.of(2023, 11, 26, 12, 10, 00),
                item,
                itemOwner
        );

        BookingDto bookingDto = BookingMapper.toBookingDto(booking);

        // When
        when(bookingController
                .saveBooking(bookingDto, 1L))
                .thenThrow(new InvalidItemOwnerException("Invalid item owner exception"));

        // Then
        mockMvc.perform(
                post("/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequestAttemptBookingDto.write(bookingDto).getJson())
                        .header("X-Sharer-User-Id", 1))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error")
                        .value("Invalid item owner exception"));
    }

    @Test
    void checkInvalidBookingDateExceptionsAreCaughtAndStatus400WhenEndIsBeforeStart() throws Exception {
        // Given
        Booking booking = new Booking(
                LocalDateTime.of(2023, 11, 25, 12, 10, 00),
                LocalDateTime.of(2023, 10, 26, 12, 10, 00),
                item,
                booker
        );

        BookingDto bookingDto = BookingMapper.toBookingDto(booking);

        // When
        when(bookingController
                .saveBooking(bookingDto, 2L))
                .thenThrow(new InvalidBookingDateException("Invalid booking date exception"));

        // Then
        mockMvc.perform(
                post("/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequestAttemptBookingDto.write(bookingDto).getJson())
                        .header("X-Sharer-User-Id", 2))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error")
                        .value("Invalid booking date exception"));
    }

    @Test
    void checkInvalidBookingDateExceptionsAreCaughtAndStatus400WhenEndIsEqualStart() throws Exception {
        // Given
        Booking booking = new Booking(
                LocalDateTime.of(2023, 10, 26, 12, 10, 00),
                LocalDateTime.of(2023, 10, 26, 12, 10, 00),
                item,
                booker
        );

        BookingDto bookingDto = BookingMapper.toBookingDto(booking);

        // When
        when(bookingController
                .saveBooking(bookingDto, 2L))
                .thenThrow(new InvalidBookingDateException("Invalid booking date exception"));

        // Then
        mockMvc.perform(
                        post("/bookings")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(jsonRequestAttemptBookingDto.write(bookingDto).getJson())
                                .header("X-Sharer-User-Id", 2))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error")
                        .value("Invalid booking date exception"));
    }

    @Test
    void checkInvalidBookingDateExceptionsAreCaughtAndStatus400WhenStartIsBeforeNow() throws Exception {
        // Given
        Booking booking = new Booking(
                LocalDateTime.of(2023, 11, 25, 12, 10, 00),
                LocalDateTime.of(2023, 12, 26, 12, 10, 00),
                item,
                booker
        );

        BookingDto bookingDto = BookingMapper.toBookingDto(booking);

        // When
        when(bookingController
                .saveBooking(bookingDto, 2L))
                .thenThrow(new InvalidBookingDateException("Invalid booking date exception"));

        // Then
        mockMvc.perform(
                        post("/bookings")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(jsonRequestAttemptBookingDto.write(bookingDto).getJson())
                                .header("X-Sharer-User-Id", 2))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error")
                        .value("Invalid booking date exception"));
    }

    @Test
    void checkBookingNotFoundExceptionsAreCaughtAndStatus404() throws Exception {
        // Given
        when(bookingController
                .getBookingById(999L, 2L))
                .thenThrow(new BookingNotFoundException("Booking not found exception"));

        // When
        mockMvc.perform(
                get("/bookings/999").accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 2))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error")
                        .value("Booking not found exception"));
    }

    @Test
    void checkUnsupportedBookingStatusExceptionsByBookerAreCaughtAndStatus400() throws Exception {
//        // Given
//        Booking booking = new Booking(
//                LocalDateTime.of(2023, 11, 25, 12, 10, 00),
//                LocalDateTime.of(2023, 12, 26, 12, 10, 00),
//                item,
//                booker
//        );

        // When
        when(bookingController
                .getAllBookingsByBooker(0, 5, "UNSUPPORTED", 2L))
                .thenThrow(new UnsupportedBookingStatusException("Unsupported status exception"));

        mockMvc.perform(
                get("/bookings?state=UNSUPPORTED")
                        .accept(MediaType.APPLICATION_JSON)
                        .requestAttr("from", 0)
                        .requestAttr("size", 5)
                        .header("X-Sharer-User-Id", 2))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error")
                        .value("Unsupported status exception"));
    }

    @Test
    void checkUnsupportedBookingStatusExceptionsByItemOwnerAreCaughtAndStatus400() throws Exception {
//        // Given
//        Booking booking = new Booking(
//                LocalDateTime.of(2023, 11, 25, 12, 10, 00),
//                LocalDateTime.of(2023, 12, 26, 12, 10, 00),
//                item,
//                booker
//        );

        // When
        when(bookingController
                .getAllBookingsByItemOwner(0, 5, "UNSUPPORTED", 1L))
                .thenThrow(new UnsupportedBookingStatusException("Unsupported status exception"));

        mockMvc.perform(
                        get("/bookings/owner?state=UNSUPPORTED")
                                .accept(MediaType.APPLICATION_JSON)
                                .requestAttr("from", 0)
                                .requestAttr("size", 5)
                                .header("X-Sharer-User-Id", 1))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error")
                        .value("Unsupported status exception"));
    }

    @Test
    void checkBookingStatusIsAlreadyApprovedExceptionsAreCaughtAndStatus400() throws Exception {
        // Given
        Booking booking = new Booking(
                LocalDateTime.of(2023, 11, 25, 12, 10, 00),
                LocalDateTime.of(2023, 12, 26, 12, 10, 00),
                item,
                booker
        );

        booking.setId(1L);
        booking.setStatus(BookingStatus.APPROVED);

        BookingDto bookingDto = BookingMapper.toBookingDto(booking);

        // When
        when(bookingController
                .updateBookingStatus(1L, true, 2))
                .thenThrow(new BookingStatusIsAlreadyApprovedException("Booking status is already approved exception"));

        mockMvc.perform(
                patch("/bookings/1?approved=true")
                        .contentType(MediaType.APPLICATION_JSON)
                        .contentType(jsonRequestAttemptBookingDto.write(bookingDto).getJson())
                        .header("X-Sharer-User-Id", 2))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error")
                        .value("Booking status is already approved exception"));
    }



    /*@Test
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

    }*/
}
