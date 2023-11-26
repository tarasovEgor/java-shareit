package ru.practicum.shareit.request.service;

import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.List;

public interface ItemRequestService {

    ItemRequestDto saveItemRequest(ItemRequest itemRequest, long userId);

    List<ItemRequestDto> getAllItemRequestsByUser(long userId);

    List<ItemRequestDto> getAllItemRequests(PageRequest pageRequest, long userId);

    ItemRequestDto getItemRequestById(long requestId, long userId);
}
