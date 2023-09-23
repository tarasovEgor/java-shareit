package ru.practicum.shareit.request.model;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDate;

@Data
@Builder(toBuilder = true)
public class ItemRequest {
    private final Long id;
    private final String description;
    private final User requestor;
    private final LocalDate created;
}
