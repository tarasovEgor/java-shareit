package ru.practicum.shareit.validation;

import org.apache.commons.lang3.EnumUtils;

import ru.practicum.shareit.booking.constant.BookingState;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.exception.BookingNotFoundException;
import ru.practicum.shareit.exception.InvalidBookingDateException;
import ru.practicum.shareit.exception.UnsupportedBookingStatusException;

import java.time.LocalDateTime;

import java.util.Optional;

public class BookingValidation {

    public static void isBookingDateValid(LocalDateTime start, LocalDateTime end) {
        if (end.isBefore(start)) {
            throw new InvalidBookingDateException("Invalid booking date.");
        }
        if (start.isEqual(end)) {
            throw new InvalidBookingDateException("Invalid booking date.");
        }
        if (start.isBefore(LocalDateTime.now())) {
            throw new InvalidBookingDateException("Invalid booking date.");
        }
    }

    public static boolean optionalOfBookingIsNotEmpty(Optional<Booking> booking) {
        if (booking.isEmpty()) {
            throw new BookingNotFoundException("Booking doesn't exist.");
        } else {
            return true;
        }
    }

    public static boolean isBookingStateValid(String bookingState) {
        if (bookingState == null) {
            return true;
        }
        if (EnumUtils.isValidEnum(BookingState.class, bookingState)) {
            return true;
        }
        throw new UnsupportedBookingStatusException("Unknown state: UNSUPPORTED_STATUS");
    }
}
