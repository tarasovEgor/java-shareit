package ru.practicum.shareit.booking.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.service.BookingService;

import java.util.List;

@RestController
@RequestMapping(path = "/bookings")
public class BookingController {

    private final BookingService bookingService;

    @Autowired
    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @PostMapping
    public BookingDto saveBooking(@RequestBody BookingDto bookingDto,
                               @RequestHeader("X-Sharer-User-Id") long bookerId) {
        return bookingService.saveBooking(bookingDto, bookerId);
    }

    @PatchMapping("/{bookingId}")
    public BookingDto updateBookingStatus(@PathVariable long bookingId,
                                       @RequestParam Boolean approved,
                                       @RequestHeader("X-Sharer-User-Id") long bookerId) {
        return bookingService.updateBookingStatus(bookingId, approved, bookerId);
    }

    @GetMapping("/{bookingId}")
    public BookingDto getBookingById(@PathVariable long bookingId,
                                  @RequestHeader("X-Sharer-User-Id") long bookerId) {
        return bookingService.getBookingById(bookingId, bookerId);
    }

    @GetMapping
    public List<BookingDto> getAllBookingsByBooker(@RequestParam(defaultValue = "0") int from,
                                                   @RequestParam(defaultValue = "5") int size,
                                                   @RequestParam(required = false) String state,
                                                   @RequestHeader("X-Sharer-User-Id") long bookerId) {
        return bookingService.getAllBookingsByBooker(state, bookerId, from, size);
    }

    @GetMapping("/owner")
    public List<BookingDto> getAllBookingsByItemOwner(@RequestParam(defaultValue = "0") int from,
                                                      @RequestParam(defaultValue = "5") int size,
                                                      @RequestParam(required = false) String state,
                                                      @RequestHeader("X-Sharer-User-Id") long ownerId) {
        return bookingService.getAllBookingsByItemOwner(state, ownerId, from, size);
    }
}

