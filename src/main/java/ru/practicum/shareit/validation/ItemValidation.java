package ru.practicum.shareit.validation;

import ru.practicum.shareit.exception.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
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

    public static boolean isItemDtoValid(ItemDto itemDto) {
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

    public static Item isItemValidForUpdate(ItemDto itemDto,
                                            long itemId,
                                            User owner,
                                            ItemRepository itemRepository) {
        Optional<Item> item;
        if (itemDto.getName() != null &&
                itemDto.getDescription() != null &&
                itemDto.getAvailable() != null) {
            itemRepository.updateItem(
                    itemDto.getName(),
                    itemDto.getDescription(),
                    itemDto.getAvailable(),
                    itemId,
                    owner);
            item = Optional.of(ItemMapper.toItem(itemDto, owner));
            item.get().setId(itemId);
            return item.get();
        } else if (itemDto.getName() != null) {
            item = Optional.ofNullable(itemRepository.findById(itemId)
                    .orElseThrow(() -> new ItemDoesNotExistException("Item doesn't exist.")));
            item.get().setName(itemDto.getName());
            itemRepository.updateItemName(itemDto.getName(), itemId, owner);
            return item.get();
        } else if (itemDto.getDescription() != null) {
            item = Optional.ofNullable(itemRepository.findById(itemId)
                    .orElseThrow(() -> new ItemDoesNotExistException("Item doesn't exist.")));
            item.get().setDescription(itemDto.getDescription());
            itemRepository.updateItemDescription(itemDto.getDescription(), itemId, owner);
            return item.get();
        } else if (itemDto.getAvailable() != null) {
            item = Optional.ofNullable(itemRepository.findById(itemId)
                    .orElseThrow(() -> new ItemDoesNotExistException("Item doesn't exist.")));
            item.get().setAvailable(itemDto.getAvailable());
            itemRepository.updateItemAvailable(itemDto.getAvailable(), itemId, owner);
            return item.get();
        }

        return null;
    }

    public static boolean ownerExists(Item item, Long ownerId) {
        if (!Objects.equals(item.getOwner(), ownerId)) {
            throw new OwnerNotFoundException("Invalid owner's id.");
        }
        return true;
    }


}
