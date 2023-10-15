package ru.practicum.shareit.booking.mapper;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingWithBookerIdDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.List;

public class BookingMapper {

    public static Booking toBooking(BookingDto bookingDto, Item item, User booker) {
        return new Booking(
                bookingDto.getStart(),
                bookingDto.getEnd(),
                item,
                booker
        );
    }

    public static BookingDto toBookingDto(Booking booking) {
        return new BookingDto(
            booking.getId(),
                booking.getItem().getId(),
                booking.getStart(),
                booking.getEnd(),
                booking.getItem(),
                booking.getBooker(),
                booking.getStatus()
        );
    }

    public static List<BookingDto> toBookingDto(List<Booking> bookings) {
        List<BookingDto> dtos = new ArrayList<>();
        for (Booking b: bookings) {
            dtos.add(toBookingDto(b));
        }
        return dtos;
    }

    public static BookingWithBookerIdDto toBookingWithBookerIdDto(Booking booking) {
        return new BookingWithBookerIdDto(
                booking.getId(),
                booking.getStart(),
                booking.getEnd(),
                booking.getItem(),
                booking.getBooker().getId(),
                booking.getStatus()
        );
    }

    public static List<BookingWithBookerIdDto> toBookingWithBookerIdDto(List<Booking> bookings) {
        List<BookingWithBookerIdDto> dtos = new ArrayList<>();
        for (Booking b : bookings) {
            dtos.add(toBookingWithBookerIdDto(b));
        }
        return dtos;
    }

}
