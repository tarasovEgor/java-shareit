package ru.practicum.shareit.item.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemWithBookingDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;

import java.util.List;

@RestController
@RequestMapping("/items")
public class ItemController {

    private ItemService itemService;

    @Autowired
    public ItemController(ItemService itemService) {
        this.itemService = itemService;
    }

    @PostMapping
    public Item saveItem(@RequestBody ItemDto itemDto, @RequestHeader("X-Sharer-User-Id") long ownerId) {
        return itemService.saveItem(itemDto, ownerId);
    }

    @PatchMapping("/{itemId}")
    public Item updateItem(@RequestBody ItemDto itemDto,
                           @PathVariable long itemId,
                           @RequestHeader("X-Sharer-User-Id") long ownerId) {
        return itemService.updateItem(itemDto, itemId, ownerId);
    }

    @GetMapping("/{itemId}")
    public ItemWithBookingDto getItem(@PathVariable long itemId,
                                      @RequestHeader("X-Sharer-User-Id") long ownerId) {
        return itemService.getItemById(itemId, ownerId);
    }

    @GetMapping
    public List<ItemWithBookingDto> getAllItems(@RequestHeader("X-Sharer-User-Id") long ownerId) {
        return itemService.getAllItems(ownerId);
    }

    @GetMapping("/search")
    public List<ItemDto> searchItem(@RequestParam String text) {
        return itemService.searchItem(text);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto saveComment(@RequestBody Comment comment,
                               @PathVariable long itemId,
                               @RequestHeader("X-Sharer-User-Id") long authorId) {
        return itemService.saveComment(comment, itemId, authorId);
    }

}
