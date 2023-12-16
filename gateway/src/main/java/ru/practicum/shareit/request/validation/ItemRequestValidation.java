package ru.practicum.shareit.request.validation;

import ru.practicum.shareit.request.exception.InvalidItemRequestDescriptionException;
import ru.practicum.shareit.request.model.ItemRequest;

public class ItemRequestValidation {
    public static boolean isItemRequestValid(ItemRequest itemRequest) {
        if (itemRequest.getDescription() == null)
            throw new InvalidItemRequestDescriptionException("Item request description cannot be null.");
        else return true;
    }
}
