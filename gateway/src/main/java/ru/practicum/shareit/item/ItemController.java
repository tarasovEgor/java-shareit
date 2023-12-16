package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;

import javax.validation.Valid;
import javax.validation.constraints.Positive;

@Controller
@RequestMapping(path = "/items")
@RequiredArgsConstructor
@Slf4j
@Validated
public class ItemController {

    private final ItemClient itemClient;

    @PostMapping
    public ResponseEntity<Object> saveItem(@Valid @RequestBody Item item,
                                           @RequestHeader("X-Sharer-User-Id") long ownerId) {
        log.info("Creating item {}", item);
        return itemClient.saveItem(item, ownerId);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> getItemById(@Positive @PathVariable long itemId,
                                              @RequestHeader("X-Sharer-User-Id") long ownerId) {
        log.info("Get item {}", itemId);
        return itemClient.getItemById(itemId, ownerId);
    }

    @GetMapping
    public ResponseEntity<Object> getAllItems(@RequestHeader("X-Sharer-User-Id") long ownerId) {
        log.info("Get all");
        return itemClient.getAllItems(ownerId);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> updateItem(@RequestBody Item item,
                                             @Positive @PathVariable long itemId,
                                             @RequestHeader("X-Sharer-User-Id") long ownerId) {
        log.info("Updating item {}, new item - {}", itemId, item);
        return itemClient.updateItem(item, itemId, ownerId);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> searchItem(@RequestParam String text,
                                             @RequestHeader("X-Sharer-User-Id") long userId) {
        log.info("Searching items in - {}", text);
        return itemClient.searchItem(text, userId);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> saveComment(@Valid @RequestBody Comment comment,
                                              @Positive @PathVariable long itemId,
                                              @RequestHeader("X-Sharer-User-Id") long authorId) {
        log.info("Saving comment - {}", comment);
        return itemClient.saveComment(comment, itemId, authorId);
    }

}
