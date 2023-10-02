package ru.practicum.shareit.validation;

import ru.practicum.shareit.exception.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.util.*;

public class ItemValidation {
    public static boolean isItemValid(Item item) {
        return true;
    }

    public static boolean isItemDtoValid(ItemDto itemDto, long ownerId, List<User> users) {
        LinkedList<Long> userIds = new LinkedList<>();

        for (User u : users) {
            userIds.add(u.getId());
        }

        if (!userIds.contains(ownerId)) {
            throw new OwnerNotFoundException("Invalid owner id. Owner seems to not exist.");
        }
        if (itemDto.getName().isEmpty()) {
            throw new InvalidItemNameException("Item name can't be empty.");
        }
        if (itemDto.getDescription() == null) {
            throw new InvalidItemDescriptionException("Item description can't be null.");
        }
        if (itemDto.getAvailable() == null) {
            throw new InvalidAvailableFieldException("Invalid field can't be null.");
        }
        return true;
    }

    public static Item isItemValidForUpdate(Item item, ItemDto itemDto, Map<Long, Item> items) {
        if (itemDto.getName() != null &&
                itemDto.getDescription() != null &&
                itemDto.getAvailable() != null) {
            item.setName(itemDto.getName());
            item.setDescription(itemDto.getDescription());
            item.setAvailable(itemDto.getAvailable());
        } else if (itemDto.getName() != null && itemDto.getDescription() != null) {
            item.setName(itemDto.getName());
            item.setDescription(itemDto.getDescription());
        } else if (itemDto.getDescription() != null && itemDto.getAvailable() != null) {
            item.setDescription(itemDto.getDescription());
            item.setAvailable(itemDto.getAvailable());
        } else if (itemDto.getName() != null && itemDto.getAvailable() != null) {
            item.setName(itemDto.getName());
            item.setAvailable(itemDto.getAvailable());
        } else if (itemDto.getName() != null) {
            item.setName(itemDto.getName());
        } else if (itemDto.getDescription() != null) {
            item.setDescription(itemDto.getDescription());
        } else if (itemDto.getAvailable() != null) {
            item.setAvailable(itemDto.getAvailable());
        }
        items.put(item.getId(), item);
        return item;
    }

    public static boolean ownerExists(Item item, Long ownerId) {
        if (!Objects.equals(item.getOwner(), ownerId)) {
            throw new OwnerNotFoundException("Invalid owner's id.");
        }
        return true;
    }
}
