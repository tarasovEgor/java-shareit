package ru.practicum.shareit.booking.dto;

import lombok.Data;

import ru.practicum.shareit.booking.constant.BookingStatus;
import ru.practicum.shareit.item.model.Item;

import java.time.LocalDateTime;

@Data
public class BookingWithBookerIdDto {
    private Long id;
    private LocalDateTime start;
    private LocalDateTime end;
    private Item item;
    private Long bookerId;
    private BookingStatus status;

    public BookingWithBookerIdDto() {

    }

    public BookingWithBookerIdDto(Long id, LocalDateTime start, LocalDateTime end, Item item,
                                  Long bookerId, BookingStatus status) {
        this.id = id;
        this.start = start;
        this.end = end;
        this.item = item;
        this.bookerId = bookerId;
        this.status = status;
    }
}