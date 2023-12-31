package ru.practicum.shareit.item.dto;

import lombok.Data;

import ru.practicum.shareit.booking.dto.BookingWithBookerIdDto;

import java.util.List;

@Data
public class ItemWithBookingDto {
    private Long id;
    private String name;
    private String description;
    private Boolean available;
    private Long request;
    private BookingWithBookerIdDto nextBooking;
    private BookingWithBookerIdDto lastBooking;
    private List<CommentDto> comments;

    public ItemWithBookingDto(Long id, String name, String description, Boolean available,
                              Long request, BookingWithBookerIdDto nextBooking,
                              BookingWithBookerIdDto lastBooking, List<CommentDto> comments) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.available = available;
        this.request = request;
        this.nextBooking = nextBooking;
        this.lastBooking = lastBooking;
        this.comments = comments;
    }
}
