package ru.practicum.shareit.item.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.impl.ItemServiceImpl;

import java.util.List;

@RestController
@RequestMapping("/items")
public class ItemController {

    private ItemServiceImpl itemServiceImpl;

    @Autowired
    public ItemController(ItemServiceImpl itemServiceImpl) {
        this.itemServiceImpl = itemServiceImpl;
    }

    @PostMapping
    public Item saveItem(@RequestBody ItemDto itemDto, @RequestHeader("X-Sharer-User-Id") long ownerId) {
        return itemServiceImpl.saveItem(itemDto, ownerId);
    }

    @PatchMapping("/{itemId}")
    public Item updateItem(@RequestBody ItemDto itemDto,
                           @PathVariable long itemId,
                           @RequestHeader("X-Sharer-User-Id") long ownerId) {
        return itemServiceImpl.updateItem(itemDto, itemId, ownerId);
    }

    @GetMapping("/{itemId}")
    public ItemDto getItem(@PathVariable long itemId) {
        return itemServiceImpl.getItem(itemId);
    }

    @GetMapping
    public List<ItemDto> getAllItems(@RequestHeader("X-Sharer-User-Id") long ownerId) {
        return itemServiceImpl.getAllItems(ownerId);
    }

    @GetMapping("/search")
    public List<ItemDto> searchItem(@RequestParam String text) {
        return itemServiceImpl.searchItem(text);
    }
}
