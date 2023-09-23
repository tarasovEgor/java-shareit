package ru.practicum.shareit.item.dto;

import lombok.Data;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

@Data
public class ItemDto {
    private final Long id;
    private final String name;
    private final String description;
    private final Boolean available;
    private final Long request;

    /*public ItemDto(Long id, String name, String description, Boolean available, Long request) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.available = available;
        this.request = request;
    }*/

    /*public ItemDto(String name,
                   String description,
                   Boolean available,
                   Long request) {
        this.name = name;
        this.description = description;
        this.available = available;
        this.request = request;
    }*/
}
