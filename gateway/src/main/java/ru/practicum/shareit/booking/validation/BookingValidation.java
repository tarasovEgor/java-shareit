package ru.practicum.shareit.booking.validation;

import org.apache.commons.lang3.EnumUtils;
import ru.practicum.shareit.booking.exception.InvalidBookingDateException;
import ru.practicum.shareit.booking.exception.UnsupportedBookingStatusException;
import ru.practicum.shareit.booking.model.BookingStatus;

import java.time.LocalDateTime;

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

    public static boolean isBookingStateValid(String bookingState) {
        if (bookingState == null) {
            return true;
        }
        if (EnumUtils.isValidEnum(BookingStatus.class, bookingState)) {
            return true;
        }
        throw new UnsupportedBookingStatusException("Unknown state: UNSUPPORTED_STATUS");
    }
}
