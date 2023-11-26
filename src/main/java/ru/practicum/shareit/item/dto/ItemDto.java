package ru.practicum.shareit.item.dto;

import lombok.Data;

import ru.practicum.shareit.request.model.ItemRequest;

@Data
public class ItemDto {
    private final Long id;
    private String name;
    private String description;
    private Boolean available;
    private Long requestId;

    public ItemDto(Long id) {
        this.id = id;
    }

    public ItemDto(Long id, String name, String description, Boolean available, Long requestId) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.available = available;
        this.requestId = requestId;
    }
}
