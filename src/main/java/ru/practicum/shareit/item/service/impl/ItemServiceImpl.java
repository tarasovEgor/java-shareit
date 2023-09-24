package ru.practicum.shareit.item.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ru.practicum.shareit.item.dao.ItemDao;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;

import java.util.List;

@Service
public class ItemServiceImpl implements ItemService {

    private ItemDao itemDao;
    private ItemService itemService;

    @Autowired
    public ItemServiceImpl(ItemDao itemDao, ItemService itemService) {
        this.itemDao = itemDao;
        this.itemService = itemService;
    }

    public Item saveItem(ItemDto itemDto, long ownerId) {
        return itemDao.saveItem(itemDto, ownerId);
    }

    public Item updateItem(ItemDto itemDto, long itemId, Long ownerId) {
        return itemDao.updateItem(itemDto, itemId, ownerId);
    }

    public ItemDto getItem(long itemId) {
        return itemDao.getItem(itemId);
    }

    public List<ItemDto> getAllItems(long ownerId) {
        return itemDao.getAllItems(ownerId);
    }

    @Override
    public List<ItemDto> searchItem(String text) {
        return itemService.searchItem(text);
    }

}
