package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.constant.BookingState;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;

import java.util.List;

public interface BookingService {

    Booking saveBooking(BookingDto bookingDto, long bookerId);

    Booking updateBookingStatus(long bookingId, Boolean status, long bookerId);

    Booking getBookingById(long bookingId, long bookerId);

    List<BookingDto> getAllBookingsByBooker(String state, long bookerId);

    List<BookingDto> getAllBookingsByItemOwner(String state, long ownerId);

}
