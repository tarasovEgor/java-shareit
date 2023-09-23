package ru.practicum.shareit.item.model;

import lombok.Builder;
import lombok.Data;

import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

@Data
public class Item {
    private Long id = 0L;
    private String name;
    private String description;
    private Boolean available;
    private Long owner;
    private Long request;

    public Item(String name,
                String description,
                Boolean available,
                Long owner,
                Long request) {
        this.name = name;
        this.description = description;
        this.available = available;
        this.owner = owner;
        this.request = request;
    }

    /*public Item(String name,
                String description,
                Boolean available,
                Long owner) {
        this.name = name;
        this.description = description;
        this.available = available;
        this.owner = owner;
    }*/


}
