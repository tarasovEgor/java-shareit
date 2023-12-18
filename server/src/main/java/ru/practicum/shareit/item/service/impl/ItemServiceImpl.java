package ru.practicum.shareit.item.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ru.practicum.shareit.booking.constant.BookingStatus;
import ru.practicum.shareit.booking.dto.BookingWithBookerIdDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemWithBookingDto;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.validation.ItemValidation;

import java.util.List;
import java.util.Optional;

import java.time.LocalDateTime;

@Service
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;

    @Autowired
    public ItemServiceImpl(ItemRepository itemRepository,
                           UserRepository userRepository,
                           BookingRepository bookingRepository,
                           CommentRepository commentRepository) {
        this.itemRepository = itemRepository;
        this.userRepository = userRepository;
        this.bookingRepository = bookingRepository;
        this.commentRepository = commentRepository;
    }

    @Override
    public ItemDto saveItem(Item item, long ownerId) {
        User owner = userRepository.findById(ownerId)
                .orElseThrow(() -> new UserDoesNotExistException("Owner doesn't exist."));
        item.setOwner(owner);
        return ItemMapper.toItemDto(itemRepository.save(item));
    }

    @Override
    public CommentDto saveComment(Comment comment, long itemId, long authorId) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new ItemDoesNotExistException("Item doesn't exist."));
        Optional<User> author = userRepository.findById(authorId);

        List<Booking> bookings = bookingRepository
                .findBookingsByItemAndOwnerOrderByStartDesc(item.getId(), author.get(), LocalDateTime.now());

        if (!bookings.isEmpty()) {
            comment.setItem(item);
            comment.setAuthor(author.get());
            comment = commentRepository.save(comment);
        } else {
            throw new NoBookingForCommentException("No booking for author was found.");
        }

        return CommentMapper.toCommentDto(
                comment,
                item,
                author.get().getName()
        );
    }

    @Override
    @Transactional
    public ItemDto updateItem(Item item, long itemId, long ownerId) {
        User owner = userRepository.findById(ownerId)
                .orElseThrow(() -> new UserDoesNotExistException("User doesn't exist."));
        return ItemValidation.isItemValidForUpdate(item, itemId, owner, itemRepository);
    }

    @Override
    public ItemWithBookingDto getItemById(long itemId, long ownerId) {
        Optional<Item> item = itemRepository.findById(itemId);
        if (item.isEmpty()) {
            throw new ItemDoesNotExistException("Item doesn't exist.");
        }

        User owner = userRepository.findById(ownerId)
                        .orElseThrow(() -> new UserDoesNotExistException("User doesn't exist."));

        Booking nextBooking = bookingRepository
                .findFirstByItemOwnerAndStartIsAfterAndStatusOrderByStartAsc(owner,
                        LocalDateTime.now(), BookingStatus.APPROVED);

        Booking lastBooking = bookingRepository
                .findFirstByItemOwnerAndStartIsBeforeAndStatusOrderByStartDesc(owner,
                        LocalDateTime.now(), BookingStatus.APPROVED);

        BookingWithBookerIdDto nextBookingWithBookerId = null;
        BookingWithBookerIdDto lastBookingWithBookerId = null;

        if (nextBooking != null && lastBooking != null) {
            nextBookingWithBookerId = BookingMapper.toBookingWithBookerIdDto(nextBooking);
            lastBookingWithBookerId = BookingMapper.toBookingWithBookerIdDto(lastBooking);
        } else if (nextBooking != null) {
            nextBookingWithBookerId = BookingMapper.toBookingWithBookerIdDto(nextBooking);
        } else if (lastBooking != null) {
            lastBookingWithBookerId = BookingMapper.toBookingWithBookerIdDto(lastBooking);
        }

        ItemWithBookingDto itemWithBookingDto = ItemMapper.toItemDtoWithBookings(item.get(),
                nextBookingWithBookerId, lastBookingWithBookerId,
                CommentMapper.toCommentDto(commentRepository.findAll()));

        return itemWithBookingDto;
    }

    @Override
    public List<ItemWithBookingDto> getAllItems(long ownerId) {
        User owner = userRepository.findById(ownerId)
                .orElseThrow(() -> new UserDoesNotExistException("User doesn't exist."));

        List<Item> items = itemRepository.findAllByOwnerOrderByIdAsc(owner);
        List<Booking> bookings = bookingRepository.findAllBookingsByItemOwner(owner);

        if (bookings != null) {
            return ItemMapper
                    .toItemDtoWithBookings(items, bookings, owner, itemRepository, bookingRepository,
                            CommentMapper.toCommentDto(commentRepository.findAll()));
        } else {
            return null;
        }
    }

    @Override
    public List<ItemDto> searchItem(String text) {
        return ItemMapper.toItemDto(itemRepository.search(text));
    }
}
