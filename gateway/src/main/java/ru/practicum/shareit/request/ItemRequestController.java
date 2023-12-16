package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

import ru.practicum.shareit.request.model.ItemRequest;

@Controller
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@Slf4j
@Validated
public class ItemRequestController {

    private final ItemRequestClient itemRequestClient;

    @PostMapping
    public ResponseEntity<Object> saveItemRequest(@Valid @RequestBody ItemRequest itemRequest,
                                                  @RequestHeader("X-Sharer-User-Id") long userId) {
        log.info("Creating item request - {}", itemRequest);
        return itemRequestClient.saveItemRequest(itemRequest, userId);
    }

    @GetMapping
    public ResponseEntity<Object> getAllItemRequestsByUser(@Positive @RequestHeader("X-Sharer-User-Id") long userId) {
        log.info("Getting all for user - {}", userId);
        return itemRequestClient.getAllItemRequestsByUser(userId);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> getItemRequestById(@Positive @PathVariable long requestId,
                                                     @Positive @RequestHeader("X-Sharer-User-Id") long userId) {
        log.info("Getting item request - {}", requestId);
        return itemRequestClient.getItemRequestById(requestId, userId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getAllItemRequests(
            @PositiveOrZero @RequestParam(name = "from", defaultValue =  "0") int from,
            @Positive @RequestParam(name = "size", defaultValue = "1") int size,
            @Positive @RequestHeader("X-Sharer-User-Id") long userId) {
        log.info("Getting item  requests for user - {}, from - {}, size - {}", from, userId, size);
        return itemRequestClient.getAllItemRequests(from, size, userId);
    }

}
