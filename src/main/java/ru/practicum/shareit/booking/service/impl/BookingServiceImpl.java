package ru.practicum.shareit.booking.service.impl;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import java.time.LocalDateTime;

@Service
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    @Autowired
    public BookingServiceImpl(BookingRepository bookingRepository,
                              UserRepository userRepository,
                              ItemRepository itemRepository) {
        this.bookingRepository = bookingRepository;
        this.userRepository = userRepository;
        this.itemRepository = itemRepository;
    }

    @Override
    public BookingDto saveBooking(BookingDto bookingDto, long bookerId) {
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

        return BookingMapper.toBookingDto(
                bookingRepository.save(BookingMapper.toBooking(
                        bookingDto,
                        item.get(),
                        booker.get()
                ))
        );
    }

    @Override
    @Transactional
    public BookingDto updateBookingStatus(long bookingId, Boolean status, long ownerId) {
        Optional<User> owner = userRepository.findById(ownerId);
        Optional<Booking> booking = bookingRepository.findById(bookingId);

        UserValidation.optionalOfUserIsNotEmpty(owner);
        BookingValidation.optionalOfBookingIsNotEmpty(booking);

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
        return BookingMapper.toBookingDto(booking.get());
    }

    @Override
    public BookingDto getBookingById(long bookingId, long bookerId) {
        Optional<User> booker = userRepository.findById(bookerId);
        Optional<Booking> booking = bookingRepository.findById(bookingId);

        UserValidation.optionalOfUserIsNotEmpty(booker);
        BookingValidation.optionalOfBookingIsNotEmpty(booking);

        if (Objects.equals(booker.get().getId(), booking.get().getBooker().getId())) {
            return BookingMapper.toBookingDto(
                    bookingRepository.findBookingByIdAndBooker(bookingId, booker.get())
            );
        } else if (Objects.equals(booker.get().getId(), booking.get().getItem().getOwner().getId())) {
            return BookingMapper.toBookingDto(
                    bookingRepository.findBookingByIdAndItemOwner(bookingId, booker.get())
            );
        } else {
            throw new BookingNotFoundException("Booking doesn't exist.");
        }
    }

/*    @Override
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
    }*/

    @Override
    public List<BookingDto> getAllBookingsByBooker(String status, long bookerId, Integer from, Integer size) {
        BookingValidation.isBookingStateValid(status);
        Page<Booking> page;
        User booker = userRepository.findById(bookerId)
                .orElseThrow(() -> new UserDoesNotExistException("User doesn't exist."));
        if (status == null && (from != null && size != null)) {
            page = bookingRepository
                    .findAllByBookerOrderByStartDesc(booker, PageRequest.of(from, size));
            return BookingMapper.toBookingDto(page.getContent());
        }
        switch (Objects.requireNonNull(status)) {
            case ("WAITING"):
                page = bookingRepository
                        .findAllByBookerAndStatusOrderByStartDesc(
                                booker, BookingStatus.WAITING, PageRequest.of(from, size));
                return BookingMapper.toBookingDto(page.getContent());
            case ("REJECTED"):
                page = bookingRepository
                        .findAllByBookerAndStatusOrderByStartDesc(
                                booker, BookingStatus.REJECTED, PageRequest.of(from, size));
                return BookingMapper.toBookingDto(page.getContent());
            case ("CURRENT"):
                page = bookingRepository
                        .findAllByBookerAndStartLessThanEqualAndEndGreaterThanEqualOrderByStartDesc(
                                booker, LocalDateTime.now(), LocalDateTime.now(), PageRequest.of(from, size));
                return BookingMapper.toBookingDto(page.getContent());
            case ("PAST"):
                page = bookingRepository
                        .findAllByBookerAndStartLessThanAndEndLessThanEqualOrderByStartDesc(
                                booker, LocalDateTime.now(), LocalDateTime.now(), PageRequest.of(from, size));
                return BookingMapper.toBookingDto(page.getContent());
            default:
                page = bookingRepository
                        .findAllByBookerOrderByStartDesc(booker, PageRequest.of(from, size));
                return BookingMapper.toBookingDto(page.getContent());
        }
    }


/*    @Override
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
    }*/

    @Override
    public List<BookingDto> getAllBookingsByItemOwner(String status, long ownerId, Integer from, Integer size) {
        BookingValidation.isBookingStateValid(status);
        Page<Booking> page;
        User owner = userRepository.findById(ownerId)
                .orElseThrow(() -> new UserDoesNotExistException("User doesn't exist."));
        if (status == null && (from != null && size != null)) {
            page = bookingRepository
                    .findAllByItemOwnerOrderByStartDesc(owner, PageRequest.of(from, size));
            return BookingMapper.toBookingDto(page.getContent());
        }
        switch (Objects.requireNonNull(status)) {
            case ("WAITING"):
                page = bookingRepository
                        .findAllByItemOwnerAndStatusOrderByStartDesc(
                                owner, BookingStatus.WAITING, PageRequest.of(from, size));
                return BookingMapper.toBookingDto(page.getContent());
            case ("REJECTED"):
                page = bookingRepository
                        .findAllByItemOwnerAndStatusOrderByStartDesc(
                                owner, BookingStatus.REJECTED, PageRequest.of(from, size));
                return BookingMapper.toBookingDto(page.getContent());
            case ("CURRENT"):
                page = bookingRepository
                        .findAllByItemOwnerAndStartLessThanEqualAndEndGreaterThanEqualOrderByStartDesc(
                                owner, LocalDateTime.now(), LocalDateTime.now(), PageRequest.of(from, size));
                return BookingMapper.toBookingDto(page.getContent());
            case ("PAST"):
                page = bookingRepository
                        .findAllByItemOwnerAndStartLessThanAndEndLessThanEqualOrderByStartDesc(
                                owner, LocalDateTime.now(), LocalDateTime.now(), PageRequest.of(from, size));
                return BookingMapper.toBookingDto(page.getContent());
            default:
                page = bookingRepository
                        .findAllByItemOwnerOrderByStartDesc(owner, PageRequest.of(from, size));
                return BookingMapper.toBookingDto(page.getContent());
        }
    }

}
