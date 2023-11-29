package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingDto;

import java.util.List;

public interface BookingService {

    BookingDto saveBooking(BookingDto bookingDto, long bookerId);

    BookingDto updateBookingStatus(long bookingId, Boolean status, long bookerId);

    BookingDto getBookingById(long bookingId, long bookerId);

    List<BookingDto> getAllBookingsByBooker(String state, long bookerId, Integer from, Integer size);

    List<BookingDto> getAllBookingsByItemOwner(String state, long ownerId, Integer from, Integer size);

}
