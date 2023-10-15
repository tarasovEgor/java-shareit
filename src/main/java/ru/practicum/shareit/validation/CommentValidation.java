package ru.practicum.shareit.validation;

import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.ItemDoesNotExistException;
import ru.practicum.shareit.exception.NoBookingForCommentException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.util.List;

public class CommentValidation {

//    public static void isCommentValid(long itemId, User author, BookingRepository bookingRepository) {
//        Booking booking = bookingRepository.findBookingByItemAndOwnerOrderByStartDesc(itemId, author);
//
//        if (booking == null) {
//            throw new NoBookingForCommentException("No booking for author was found.");
//        }
//    }
}
