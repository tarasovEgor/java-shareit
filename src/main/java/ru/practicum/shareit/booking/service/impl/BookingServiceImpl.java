package ru.practicum.shareit.booking.service.impl;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.constant.BookingState;
import ru.practicum.shareit.booking.constant.BookingStatus;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.exception.*;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.validation.BookingValidation;
import ru.practicum.shareit.validation.UserValidation;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class BookingServiceImpl implements BookingService {

    private BookingRepository bookingRepository;
    private UserRepository userRepository;
    private ItemRepository itemRepository;

    @Autowired
    public BookingServiceImpl(BookingRepository bookingRepository,
                              UserRepository userRepository,
                              ItemRepository itemRepository) {
        this.bookingRepository = bookingRepository;
        this.userRepository = userRepository;
        this.itemRepository = itemRepository;
    }

    @Override
    public Booking saveBooking(BookingDto bookingDto, long bookerId) {
        Optional<Item> item = Optional.ofNullable(itemRepository.findById(bookingDto.getItemId())
                .orElseThrow(() -> new ItemDoesNotExistException("Item doesn't exist.")));
        if (item.get().getOwner().getId().equals(bookerId)) {
            throw new InvalidItemOwnerException("Invalid item owner.");
        }
        if (!item.get().getAvailable()) {
            throw new ItemIsUnavailableException("Item is not available.");
        }
        if (bookingDto.getStart() == null || bookingDto.getEnd() == null) {
            throw new InvalidBookingDateException("Invalid booking date.");
        }
        BookingValidation.isBookingDateValid(bookingDto.getStart(), bookingDto.getEnd());
        Optional<User> booker = userRepository.findById(bookerId);
        return bookingRepository.save(BookingMapper.toBooking(bookingDto, item.get(), booker.get()));
    }

    @Override
    @Transactional
    public Booking updateBookingStatus(long bookingId, Boolean status, long ownerId) {
        Optional<User> owner = userRepository.findById(ownerId);
        Optional<Booking> booking = bookingRepository.findById(bookingId);

        UserValidation.OptionalOfUserIsNotEmpty(owner);
        BookingValidation.OptionalOfBookingIsNotEmpty(booking);

//        if (!Objects.equals(booking.get().getBooker().getId(), booker.get().getId()) ||
//            !Objects.equals(booking.get().getItem().getOwner().getId(), booker.get())) {
//            throw new InvalidBookerOwnerException("Invalid item booker.");
//        }

        if (status) {
            if (!owner.get().getId().equals(booking.get().getItem().getOwner().getId())) {
                throw new InvalidItemOwnerException("Invalid item owner.");
            }
            if (booking.get().getStatus().equals(BookingStatus.APPROVED)) {
                throw new BookingStatusIsAlreadyApprovedException("Booking status has already been approved.");
            } else {
                bookingRepository.updateBookingStatus(BookingStatus.APPROVED, bookingId, owner.get());
                booking.get().setStatus(BookingStatus.APPROVED);
            }
        } else {
            bookingRepository.updateBookingStatus(BookingStatus.REJECTED,bookingId, owner.get());
            booking.get().setStatus(BookingStatus.REJECTED);
        }
        return booking.get();
    }

    @Override
    public Booking getBookingById(long bookingId, long bookerId) {
        Optional<User> booker = userRepository.findById(bookerId);
        Optional<Booking> booking = bookingRepository.findById(bookingId);

        UserValidation.OptionalOfUserIsNotEmpty(booker);
        BookingValidation.OptionalOfBookingIsNotEmpty(booking);

        if (Objects.equals(booker.get().getId(), booking.get().getBooker().getId())) {
            return bookingRepository.findBookingByIdAndBooker(bookingId, booker.get());
        } else if (Objects.equals(booker.get().getId(), booking.get().getItem().getOwner().getId())) {
            return bookingRepository.findBookingByIdAndItemOwner(bookingId, booker.get());
        } else {
            throw new BookingNotFoundException("Booking doesn't exist.");
        }
    }

    @Override
    public List<BookingDto> getAllBookingsByBooker(String state, long bookerId) {
        BookingValidation.isBookingStateValid(state);
        Optional<User> booker = Optional.of(userRepository.findById(bookerId))
                .orElseThrow(() -> new UserDoesNotExistException("User doesn't exist"));
        if (state != null) {
            if (state.equals(String.valueOf(BookingState.WAITING))) {
                return BookingMapper.toBookingDto(bookingRepository
                        .findAllBookingsByBookerAndStatus(booker.get(), BookingStatus.WAITING));
            } else if (state.equals(String.valueOf(BookingStatus.REJECTED))) {
                return BookingMapper.toBookingDto(bookingRepository
                        .findAllBookingsByBookerAndStatus(booker.get(), BookingStatus.REJECTED));
            } else if (state.equals(String.valueOf(BookingState.CURRENT))) {
                return BookingMapper.toBookingDto(bookingRepository
                        .findAllBookingsByBookerAndStatusCurrent(booker.get(), LocalDateTime.now()));
            } else if (state.equals(String.valueOf(BookingState.PAST))) {
                return BookingMapper.toBookingDto(bookingRepository
                        .findAllBookingsByBookerAndStatusPast(booker.get(), LocalDateTime.now()));
            }
        }
        return BookingMapper.toBookingDto(bookingRepository.findAllBookingsByBooker(booker.get()));

    }

    @Override
    public List<BookingDto> getAllBookingsByItemOwner(String state, long ownerId) {
        BookingValidation.isBookingStateValid(state);
        Optional<User> owner = Optional.of(userRepository.findById(ownerId))
                .orElseThrow(() -> new UserDoesNotExistException("User doesn't exist."));
        if (state != null) {
            if (state.equals(String.valueOf(BookingState.WAITING))) {
                return BookingMapper.toBookingDto(bookingRepository
                        .findAllBookingsByItemOwnerAndStatus(owner.get(), BookingStatus.WAITING));
            } else if (state.equals(String.valueOf(BookingStatus.REJECTED))) {
                return BookingMapper.toBookingDto(bookingRepository
                        .findAllBookingsByItemOwnerAndStatus(owner.get(), BookingStatus.REJECTED));
            } else if (state.equals(String.valueOf(BookingState.CURRENT))) {
                return BookingMapper.toBookingDto(bookingRepository
                        .findAllBookingsByItemOwnerAndStatusCurrent(owner.get(), LocalDateTime.now()));
            } else if (state.equals(String.valueOf(BookingState.PAST))) {
                return BookingMapper.toBookingDto(bookingRepository
                        .findAllBookingsByItemOwnerAndStatusPast(owner.get(), LocalDateTime.now()));
            }
        }
        return BookingMapper.toBookingDto(bookingRepository.findAllBookingsByItemOwner(owner.get()));
    }


}
