package ru.practicum.shareit.item.mapper;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;

public class ItemMapper {
   public static ItemDto toItemDto(Item item) {
       return new ItemDto(
               item.getId(),
               item.getName(),
               item.getDescription(),
               item.getAvailable(),
               item.getRequest() != null ? item.getRequest() : null
       );
   }

   public static Item toItem(ItemDto itemDto, long ownerId) {
       return new Item(
               itemDto.getName(),
               itemDto.getDescription(),
               itemDto.getAvailable(),
               ownerId,
               itemDto.getRequest() != null ? itemDto.getRequest() : null
       );
   }
}
