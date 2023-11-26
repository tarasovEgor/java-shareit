package ru.practicum.shareit.request.dto;

import lombok.Data;

import lombok.NoArgsConstructor;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
public class ItemRequestDto {
    private Long id;
    private String description;
    private User requestor;
    private List<Item> items;
    private LocalDateTime created;

    public ItemRequestDto(Long id, String description, User requestor, LocalDateTime created, List<Item> items) {
        this.id = id;
        this.description = description;
        this.requestor = requestor;
        this.created = created;
        this.items = items;
    }
}
