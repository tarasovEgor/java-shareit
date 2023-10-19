package ru.practicum.shareit.item.dto;

import lombok.Data;

import ru.practicum.shareit.request.model.ItemRequest;

@Data
public class ItemDto {
    private final Long id;
    private String name;
    private String description;
    private Boolean available;
    private ItemRequest request;

    public ItemDto(Long id) {
        this.id = id;
    }

    public ItemDto(Long id, String name, String description, Boolean available, ItemRequest request) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.available = available;
        this.request = request;
    }
}
