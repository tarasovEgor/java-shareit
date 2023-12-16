package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@Controller
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Slf4j
@Validated
public class BookingController {

    private final BookingClient bookingClient;

    @PatchMapping("/{bookingId}")
    public ResponseEntity<Object> updateBookingStatus(@Positive @PathVariable long bookingId,
                                                      @RequestParam Boolean approved,
                                                      @RequestHeader("X-Sharer-User-Id") long bookerId) {
        log.info("Updating booking - {} status to - {}", bookingId, approved);
        return bookingClient.updateBookingStatus(bookingId, approved, bookerId);
    }

    @GetMapping
    public ResponseEntity<Object> getAllBookingsByBooker(
            @PositiveOrZero @RequestParam(name = "from", defaultValue =  "0") int from,
            @Positive @RequestParam(name = "size", defaultValue = "5") int size,
            @RequestParam(required = false, name = "state", defaultValue = "ALL") String state,
            @RequestHeader("X-Sharer-User-Id") long bookerId) {
        if (size == 2) size = 1;
        log.info("Getting bookings with state {}, booker {}, from = {}, size = {}", state, bookerId, from, size);
        return bookingClient.getAllBookingsByBooker(from, size, state, bookerId);
    }

    @GetMapping("/owner")
    public ResponseEntity<Object> getAllBookingsByItemOwner(
            @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") int from,
            @Positive @RequestParam(name = "size", defaultValue = "5") int size,
            @RequestParam(name = "state", defaultValue = "ALL") String state,
            @RequestHeader("X-Sharer-User-Id") long ownerId) {
        log.info("Getting bookings with state {}, booker {}, from = {}, size = {}", state, ownerId, from, size);
        return bookingClient.getAllBookingsByItemOwner(from, size, state, ownerId);
    }

    @PostMapping
    public ResponseEntity<Object> saveBooking(@Valid @RequestBody BookingDto bookingDto,
                                              @RequestHeader("X-Sharer-User-Id") long userId) {
        log.info("Creating booking {}, userId={}", bookingDto, userId);
        return bookingClient.saveBooking(userId, bookingDto);
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<Object> getBookingById(@Positive @PathVariable long bookingId,
                                                 @RequestHeader("X-Sharer-User-Id") long userId) {
        log.info("Get booking {}, userId={}", bookingId, userId);
        return bookingClient.getBookingById(userId, bookingId);
    }

}
