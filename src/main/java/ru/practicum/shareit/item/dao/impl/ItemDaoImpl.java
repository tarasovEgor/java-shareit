package ru.practicum.shareit.item.dao.impl;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import ru.practicum.shareit.exception.ItemDoesNotExistException;
import ru.practicum.shareit.exception.OwnerNotFoundException;
import ru.practicum.shareit.item.dao.ItemDao;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.dao.UserDao;
import ru.practicum.shareit.validation.ItemValidation;

import java.util.*;

@Repository
@Slf4j
public class ItemDaoImpl implements ItemDao, ItemService {

    private Map<Long, Item> items = new HashMap<>();
    private UserDao userDao;
    private Long id = 1L;

    @Autowired
    public ItemDaoImpl(UserDao userDao) {
        this.userDao = userDao;
    }

    @Override
    public Item saveItem(ItemDto itemDto, long ownerId) {
        ItemValidation.isItemDtoValid(itemDto, ownerId, userDao.getAllUsers());
        Item item = ItemMapper.toItem(itemDto, ownerId);
        item.setId(id++);
        items.put(item.getId(), item);
        return item;
    }

    @Override
    public Item updateItem(ItemDto itemDto, long itemId, Long ownerId) {
        if (!items.containsKey(itemId)) {
            throw new ItemDoesNotExistException("Item does not exist.");
        }
        if (ownerId == null) {
            throw new OwnerNotFoundException("Invalid owner id. Owner seems to not exist.");
        }
        Item item = items.get(itemId);
        ItemValidation.ownerExists(item, ownerId);
        return ItemValidation.isItemValidForUpdate(item, itemDto, items);
    }

    @Override
    public ItemDto getItem(long itemId) {
        Item item = items.get(itemId);
        return ItemMapper.toItemDto(item);
    }

    @Override
    public List<ItemDto> getAllItems(long ownerId) {
        List<ItemDto> ownersItems = new ArrayList<>();

        for (Item i : items.values()) {
            if (i.getOwner() == ownerId) {
                ownersItems.add(ItemMapper.toItemDto(i));
            }
        }
        return ownersItems;
    }

    @Override
    public List<ItemDto> searchItem(String text) {
        List<ItemDto> matchedItems = new ArrayList<>();

        if (text.isEmpty()) {
            return matchedItems;
        }

        for (Item i : items.values()) {

            if (i.getName().replaceAll("//s", "").toLowerCase()
                            .contains(text.toLowerCase()) ||
                    i.getDescription().replaceAll("//s", "").toLowerCase()
                            .contains(text.toLowerCase())) {
                if (i.getAvailable()) {
                    matchedItems.add(ItemMapper.toItemDto(i));
                }
            } else {
                log.info("Items containing: {} were not found.", text);
            }
        }
        return matchedItems;
    }

}
