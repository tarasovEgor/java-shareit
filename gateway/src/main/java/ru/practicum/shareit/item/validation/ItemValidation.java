package ru.practicum.shareit.item.validation;

import ru.practicum.shareit.item.exception.InvalidAvailableFieldException;
import ru.practicum.shareit.item.exception.InvalidItemDescriptionException;
import ru.practicum.shareit.item.exception.InvalidItemNameException;
import ru.practicum.shareit.item.model.Item;

public class ItemValidation {

    public static boolean isItemDtoValid(Item item) {
        if (item.getName().isEmpty()) {
            throw new InvalidItemNameException("Item name can't be empty.");
        }
        if (item.getDescription() == null) {
            throw new InvalidItemDescriptionException("Item description can't be null.");
        }
        if (item.getAvailable() == null) {
            throw new InvalidAvailableFieldException("Invalid field, available can't be null.");
        }
        return true;
    }

}
