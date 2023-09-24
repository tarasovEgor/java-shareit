package ru.practicum.shareit.booking.dto;

import lombok.Data;

import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDate;

@Data
public class BookingDto {
    private final Long id;
    private final LocalDate start;
    private final LocalDate end;
    private final Item item;
    private final User booker;
    private final Boolean status;
}
