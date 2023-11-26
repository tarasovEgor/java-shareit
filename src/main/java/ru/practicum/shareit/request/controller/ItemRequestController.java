package ru.practicum.shareit.request.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.util.List;

@RestController
@RequestMapping(path = "/requests")
public class ItemRequestController {

    private final ItemRequestService itemRequestService;

    @Autowired
    public ItemRequestController(ItemRequestService itemRequestService) {
        this.itemRequestService = itemRequestService;
    }

    @PostMapping
    public ItemRequestDto saveItemRequest(@RequestBody ItemRequest itemRequest,
                                          @RequestHeader("X-Sharer-User-Id") long userId) {
        return itemRequestService.saveItemRequest(itemRequest, userId);
    }

    @GetMapping
    public List<ItemRequestDto> getAllItemRequestsByUser(@RequestHeader("X-Sharer-User-Id") long userId) {
        return itemRequestService.getAllItemRequestsByUser(userId);
    }

    @GetMapping("/{requestId}")
    public ItemRequestDto getItemRequestById(@PathVariable long requestId,
                                             @RequestHeader("X-Sharer-User-Id") long userId) {
        return itemRequestService.getItemRequestById(requestId, userId);
    }

    @GetMapping("/all")
    public List<ItemRequestDto> getAllItemRequests(@RequestParam(required = false, defaultValue = "0") int from,
                                                   @RequestParam(required = false, defaultValue = "1") int size,
                                                   @RequestHeader("X-Sharer-User-Id") long userId) {
        return itemRequestService.getAllItemRequests(PageRequest.of(from, size), userId);
    }
}
