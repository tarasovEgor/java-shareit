package ru.practicum.shareit.request.dto;

import lombok.Data;

import ru.practicum.shareit.user.model.User;

import java.time.LocalDate;

@Data
public class ItemRequestDto {
    private final Long id;
    private final String description;
    private final User requestor;
    private final LocalDate created;
}
