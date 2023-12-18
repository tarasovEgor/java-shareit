package ru.practicum.shareit.validation;

import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.exception.BookingNotFoundException;

import java.util.Optional;

public class BookingValidation {

    public static boolean optionalOfBookingIsNotEmpty(Optional<Booking> booking) {
        if (booking.isEmpty()) {
            throw new BookingNotFoundException("Booking doesn't exist.");
        } else {
            return true;
        }
    }

}
