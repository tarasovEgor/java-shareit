package ru.practicum.shareit.item.mapper;

import ru.practicum.shareit.booking.dto.BookingWithBookerIdDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemWithBookingDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import java.time.LocalDateTime;

public class ItemMapper {

    public static ItemDto toItemDto(Item item) {
       return new ItemDto(
               item.getId(),
               item.getName(),
               item.getDescription(),
               item.getAvailable(),
               item.getRequest() != null ? item.getRequest() : null

       );
   }

   public static ItemWithBookingDto toItemDtoWithBookings(Item item,
                                                          BookingWithBookerIdDto nextBooking,
                                                          BookingWithBookerIdDto lastBooking,
                                                          List<CommentDto> comments) {
       return new ItemWithBookingDto(
               item.getId(),
               item.getName(),
               item.getDescription(),
               item.getAvailable(),
               item.getRequest() != null ? item.getRequest() : null,
               nextBooking,
               lastBooking,
               comments
       );
   }

   public static List<ItemWithBookingDto> toItemDtoWithBookings(List<Item> items,
                                                                List<Booking> bookings,
                                                                User owner,
                                                                ItemRepository iRepo,
                                                                BookingRepository bRepo,
                                                                List<CommentDto> comments) {

       List<ItemWithBookingDto> dtos = new ArrayList<>();

       Optional<Item> item;

       Booking nextBooking = null;
       Booking lastBooking = null;

       for (Item i : items) {
           item = iRepo.findById(i.getId());
           for (Booking b : bookings) {
               if (Objects.equals(i.getId(), b.getItem().getId())) {
                   nextBooking = bRepo.findFirstByItemOwnerAndStartIsAfterOrderByStartAsc(owner, LocalDateTime.now());
                   lastBooking = bRepo.findFirstByItemOwnerAndStartIsBeforeOrderByStartDesc(owner, LocalDateTime.now());
               } else {
                   nextBooking = null;
                   lastBooking = null;
               }
           }

            if (nextBooking != null && lastBooking != null) {
                dtos.add(ItemMapper.toItemDtoWithBookings(
                        item.get(),
                        BookingMapper.toBookingWithBookerIdDto(nextBooking),
                        BookingMapper.toBookingWithBookerIdDto(lastBooking),
                        comments
                ));
            } else {
                dtos.add(ItemMapper.toItemDtoWithBookings(
                        item.get(),
                        null,
                        null,
                        comments
                        )
                );
            }
       }
       return dtos;
   }

    public static Item toItem(ItemDto itemDto, User owner) {
       Item item = new Item();
       item.setOwner(owner);
       item.setName(itemDto.getName());
       item.setDescription(itemDto.getDescription());
       item.setAvailable(itemDto.getAvailable());
       item.setRequest(itemDto.getRequest());
       return item;
    }

    public static List<ItemDto> toItemDto(List<Item> items) {
        List<ItemDto> dtos = new ArrayList<>();
        for (Item item : items) {
            dtos.add(toItemDto(item));
        }
        return dtos;
    }
}
