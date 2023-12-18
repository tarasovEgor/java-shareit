package ru.practicum.shareit.item.dao;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemDao {
    Item saveItem(ItemDto itemDto, long ownerId);

    Item updateItem(ItemDto itemDto, long itemId, Long ownerId);

    ItemDto getItem(long itemId);

    List<ItemDto> getAllItems(long ownerId);

    List<ItemDto> searchItem(String text);
}
