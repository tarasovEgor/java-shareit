package ru.practicum.shareit.item.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.constant.BookingState;
import ru.practicum.shareit.booking.constant.BookingStatus;
import ru.practicum.shareit.booking.dto.BookingWithBookerIdDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.*;
import ru.practicum.shareit.item.dao.ItemDao;
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
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.validation.CommentValidation;
import ru.practicum.shareit.validation.ItemValidation;
import ru.practicum.shareit.validation.UserValidation;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ItemServiceImpl implements ItemService {

    //private ItemDao itemDao;
    private ItemRepository itemRepository;
    private UserRepository userRepository;
    private BookingRepository bookingRepository;
    private CommentRepository commentRepository;

    @Autowired
    public ItemServiceImpl(ItemRepository itemRepository, UserRepository userRepository,
                           BookingRepository bookingRepository, CommentRepository commentRepository) {
        this.itemRepository = itemRepository;
        this.userRepository = userRepository;
        this.bookingRepository = bookingRepository;
        this.commentRepository = commentRepository;
    }

    @Override
    public Item saveItem(ItemDto itemDto, long ownerId) {
        //return itemDao.saveItem(itemDto, ownerId);
        ItemValidation.isItemDtoValid(itemDto);
        User owner = userRepository.findById(ownerId)
                .orElseThrow(() -> new UserDoesNotExistException("Owner doesn't exist."));
        return itemRepository.save(ItemMapper.toItem(itemDto, owner));
    }

//    @Override
//    public Comment saveComment(CommentDto commentDto, long itemId, long authorId) {
//        if (commentDto.getText().isEmpty()) {
//            throw new InvalidCommentException("Can't create a comment without text.");
//        }
//
//        Item item = itemRepository.findById(itemId)
//                .orElseThrow(() -> new ItemDoesNotExistException("Item doesn't exist."));
//        User author = userRepository.findById(authorId)
//                .orElseThrow(() -> new UserDoesNotExistException("Author doesn't exist."));
//
//       // CommentValidation.isCommentValid(item.getId(), author, bookingRepository);
//        List<Booking> booking = bookingRepository
//                //.findFirstByItemOwnerAndEndIsBeforeOrEndIsEqualOrderByStartDesc(author, LocalDateTime.now());
//                .findBookingsByItemAndOwnerOrderByStartDesc(item.getId(), author, LocalDateTime.now());
//
//        if (!booking.isEmpty()) {
//            return commentRepository.save(CommentMapper.toComment(commentDto, item, author));
//
//        } else {
//            throw new NoBookingForCommentException("No booking for author was found.");
//        }
//    }

    /////////////
    @Override
    public CommentDto saveComment(Comment comment, long itemId, long authorId) {
        if (comment.getText().isEmpty()) {
            throw new InvalidCommentException("Can't create a comment without text.");
        }

        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new ItemDoesNotExistException("Item doesn't exist."));
        Optional<User> author = userRepository.findById(authorId);
                //.orElseThrow(() -> new UserDoesNotExistException("Author doesn't exist."));

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
    /////////////

    @Override
    @Transactional
    public Item updateItem(ItemDto itemDto, long itemId, long ownerId) {
        //  return itemDao.updateItem(itemDto, itemId, ownerId);
        User owner = userRepository.findById(ownerId)
                .orElseThrow(() -> new UserDoesNotExistException("User doesn't exist."));
//        itemRepository.updateItem(
//                itemDto.getName(),
//                itemDto.getDescription(),
//                itemDto.getAvailable(),
//                itemId,
//                owner);
       // return itemRepository.getReferenceById(itemId);
//        Item item = ItemMapper.toItem(itemDto, owner);
//        item.setId(itemId);
//        return item;
        return ItemValidation.isItemValidForUpdate(itemDto, itemId, owner, itemRepository);
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
                .findFirstByItemOwnerAndStartIsAfterOrderByStartAsc(owner, LocalDateTime.now());

        Booking lastBooking = bookingRepository
                //.findFirstByItemOwnerAndStartIsBeforeOrderByStartDesc(owner, LocalDateTime.now());
                //.findBookingByItemNameAndOwner(item.get().getName(), owner, LocalDateTime.now());
                .findFirstByItemNameAndItemOwnerAndStartIsBeforeOrderByStartDesc(item.get().getName(),
                        owner, LocalDateTime.now());

        BookingWithBookerIdDto nextBookingWithBookerId = null;
        BookingWithBookerIdDto lastBookingWithBookerId = null;

        if (nextBooking != null && lastBooking != null) {
            nextBookingWithBookerId = BookingMapper.toBookingWithBookerIdDto(nextBooking);
            lastBookingWithBookerId = BookingMapper.toBookingWithBookerIdDto(lastBooking);
        }

        ItemWithBookingDto itemWithBookingDto = ItemMapper.toItemDtoWithBookings(item.get(),
                nextBookingWithBookerId, lastBookingWithBookerId,
                CommentMapper.toCommentDto(commentRepository.findAll()));

//        if (itemWithBookingDto.getNextBooking() != null) {
//            if (itemWithBookingDto.getNextBooking().getStatus().equals(BookingStatus.REJECTED)) {
//                itemWithBookingDto.setNextBooking(null);
//            }
//        }
//        if (itemWithBookingDto.getLastBooking() != null) {
//            if (itemWithBookingDto.getLastBooking().getStatus().equals(BookingStatus.REJECTED)) {
//                itemWithBookingDto.setLastBooking(null);
//            }
//        }

        return itemWithBookingDto;
//        Optional<Item> item = Optional.ofNullable(itemRepository.findById(itemId)
//                .orElseThrow(() -> new ItemDoesNotExistException("Item doesn't exist.")));
//        return ItemMapper.toItemDto(
//                item.get());

//        Optional<Item> item = itemRepository.findById(itemId);
//        if (item.isEmpty()) {
//            throw new ItemDoesNotExistException("Item doesn't exist.");
//        }
//                //.orElseThrow(() -> new ItemDoesNotExistException("Item doesn't exist."));
//        return ItemMapper.toItemDtoWithBookings(item.get(),
//                BookingWithDateMapper.toBookingWithDateDto(
//                        bookingRepository.findAllBookingsByItemIdAndStartDateInFuture(itemId)),
//                BookingWithDateMapper.toBookingWithDateDto(
//                        bookingRepository.findAllBookingsByItemAndStartDateInPast(itemId))
//                );

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

        //        for (Item i : items) {
//            itemWithBookingDtos.add(ItemMapper.toItemDtoWithBookings(
//                    itemRepository.getItemByIdAndOwner(i.getId(), owner),
//                    bookingRepository.findFirstByItemIdOwnerAndStartIsBeforeOrderByStartDesc(i.getId(), LocalDateTime.now()),
//                    bookingRepository.findFirstByItemIdOwnerAndStartIsAfterOrderByStartAsc(i.getId(), LocalDateTime.now())
//                    ));
//        }

//        itemWithBookingDtos = ItemMapper.toItemDtoWithBookings(
//                itemRepository.findAllByOwnerOrderByIdAsc(owner),
//                BookingMapper.toBookingWithBookerIdDto(bookingRepository.findAllBookingsByItemOwner(owner)),
//                BookingMapper.toBookingWithBookerIdDto(bookingRepository.)
//                )








        ////////////////////THE PART THAT WORKED BUT SHITTY////////////////////////////////
//        return ItemMapper.toItemDtoWithBookings(itemRepository.findAllByOwnerOrderByIdAsc(owner),
//                BookingMapper.toBookingWithBookerIdDto(bookingRepository
//                        .findFirstByItemOwnerAndStartIsAfterOrderByStartAsc(owner, LocalDateTime.now())),
//                BookingMapper.toBookingWithBookerIdDto(bookingRepository
//                        .findFirstByItemOwnerAndStartIsBeforeOrderByStartDesc(owner, LocalDateTime.now()))
//        );






        //return itemDao.getAllItems(ownerId);
//        List<Item> items = itemRepository.findAll();
//        return ItemMapper.toItemDto(items);
//        List<Item> ownersItems = new ArrayList<>();
//        for (Item i : itemRepository.findAll()) {
//            if (i.getOwner().getId() == ownerId) {
//                ownersItems.add(i);
//            }
//        }
//        return ItemMapper.toItemDto(ownersItems);

    }

    @Override
    public List<ItemDto> searchItem(String text) {
       // return itemDao.searchItem(text);
        if (text.isEmpty()) {
            return new ArrayList<>();
        }
        return ItemMapper.toItemDto(itemRepository.search(text));
    }



}
