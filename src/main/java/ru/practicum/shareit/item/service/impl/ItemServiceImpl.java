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

    @Autowired
    public ItemServiceImpl(ItemDao itemDao) {
        this.itemDao = itemDao;
    }

    @Override
    public Item saveItem(ItemDto itemDto, long ownerId) {
        return itemDao.saveItem(itemDto, ownerId);
    }

    @Override
    public Item updateItem(ItemDto itemDto, long itemId, Long ownerId) {
        return itemDao.updateItem(itemDto, itemId, ownerId);
    }

    @Override
    public ItemDto getItem(long itemId) {
        return itemDao.getItem(itemId);
    }

    @Override
    public List<ItemDto> getAllItems(long ownerId) {
        return itemDao.getAllItems(ownerId);
    }

    @Override
    public List<ItemDto> searchItem(String text) {
        return itemDao.searchItem(text);
    }

}
