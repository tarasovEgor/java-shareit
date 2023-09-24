package ru.practicum.shareit.booking.model;

import lombok.Builder;
import lombok.Data;

import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDate;

@Data
@Builder(toBuilder = true)
public class Booking {
    private final Long id;
    private final LocalDate start;
    private final LocalDate end;
    private final Item item;
    private final User booker;
    private final Boolean status;
}
