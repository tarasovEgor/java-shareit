package ru.practicum.shareit.request.mapper;

import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class ItemRequestMapper {

    public static ItemRequestDto toItemRequestDto(ItemRequest itemRequest, User requestor, ItemRepository iRep) {
        Optional<List<Item>> items = Optional.ofNullable(iRep.findAllByRequestId(itemRequest.getId()));
        return items.map(itemList -> new ItemRequestDto(
                itemRequest.getId(),
                itemRequest.getDescription(),
                requestor,
                itemRequest.getCreated(),
                itemList
        )).orElseGet(() -> new ItemRequestDto(
                itemRequest.getId(),
                itemRequest.getDescription(),
                requestor,
                itemRequest.getCreated(),
                List.of()
        ));
    }

    public static List<ItemRequestDto> toItemRequestDto(List<ItemRequest> itemRequests, ItemRepository iRep) {
        return itemRequests.stream()
                .map(itemRequest -> new ItemRequestDto(
                        itemRequest.getId(),
                        itemRequest.getDescription(),
                        itemRequest.getRequestor(),
                        itemRequest.getCreated(),
                        iRep.findAllByRequestId(itemRequest.getId())
                ))
                .collect(Collectors.toList());
    }

}
