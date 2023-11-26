package ru.practicum.shareit.validation;

import ru.practicum.shareit.exception.InvalidItemRequestDescriptionException;
import ru.practicum.shareit.request.model.ItemRequest;

public class ItemRequestValidation {

    public static boolean isItemRequestValid(ItemRequest itemRequest) {
        if (itemRequest.getDescription() == null)
            throw new InvalidItemRequestDescriptionException("Item request description cannot be null.");
        else return true;
    }
}
