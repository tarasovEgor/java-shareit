package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemWithBookingDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemService {

    Item saveItem(ItemDto itemDto, long ownerId);

    CommentDto saveComment(Comment comment, long itemId, long authorId);

    Item updateItem(ItemDto itemDto, long itemId, long ownerId);

    ItemWithBookingDto getItemById(long itemId, long ownerId);

    List<ItemWithBookingDto> getAllItems(long ownerId);

    List<ItemDto> searchItem(String text);


}
