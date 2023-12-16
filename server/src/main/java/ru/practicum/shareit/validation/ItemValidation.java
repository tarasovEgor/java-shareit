package ru.practicum.shareit.validation;

import ru.practicum.shareit.exception.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;

public class ItemValidation {

//    public static boolean isItemDtoValid(Item item) {
//        if (item.getName().isEmpty()) {
//            throw new InvalidItemNameException("Item name can't be empty.");
//        }
//        if (item.getDescription() == null) {
//            throw new InvalidItemDescriptionException("Item description can't be null.");
//        }
//        if (item.getAvailable() == null) {
//            throw new InvalidAvailableFieldException("Invalid field, available can't be null.");
//        }
//        return true;
//    }

    public static ItemDto isItemValidForUpdate(Item item,
                                            long itemId,
                                            User owner,
                                            ItemRepository itemRepository) {
        ItemDto itemDto = new ItemDto(itemId);
        if (item.getName() != null &&
                item.getDescription() != null &&
                item.getAvailable() != null) {

            itemRepository.updateItem(
                    item.getName(),
                    item.getDescription(),
                    item.getAvailable(),
                    itemId,
                    owner
            );

            itemDto.setName(item.getName());
            itemDto.setDescription(item.getDescription());
            itemDto.setAvailable(item.getAvailable());
            itemDto.setRequestId(item.getRequestId());

            return itemDto;

        } else if (item.getName() != null) {

            itemRepository.updateItemName(
                    item.getName(),
                    itemId, owner
            );

            item = itemRepository.findById(itemId)
                    .orElseThrow(() -> new ItemDoesNotExistException("Item doesn't exist."));

            itemDto.setName(item.getName());
            itemDto.setDescription(item.getDescription());
            itemDto.setAvailable(item.getAvailable());
            itemDto.setRequestId(item.getRequestId());

            return itemDto;

        } else if (item.getDescription() != null) {

            itemRepository.updateItemDescription(
                    item.getDescription(),
                    itemId, owner
            );

            item = itemRepository.findById(itemId)
                    .orElseThrow(() -> new ItemDoesNotExistException("Item doesn't exist."));

            itemDto.setName(item.getName());
            itemDto.setDescription(item.getDescription());
            itemDto.setAvailable(item.getAvailable());
            itemDto.setRequestId(item.getRequestId());

            return itemDto;

        } else if (item.getAvailable() != null) {

            itemRepository.updateItemAvailable(
                    item.getAvailable(),
                    itemId, owner
            );

            item = itemRepository.findById(itemId)
                    .orElseThrow(() -> new ItemDoesNotExistException("Item doesn't exist."));

            itemDto.setName(item.getName());
            itemDto.setDescription(item.getDescription());
            itemDto.setAvailable(item.getAvailable());
            itemDto.setRequestId(item.getRequestId());

            return itemDto;
        }
        return null;
    }

}
