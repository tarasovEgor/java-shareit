package ru.practicum.shareit.item.dto;

import lombok.Data;
import ru.practicum.shareit.request.model.ItemRequest;

@Data
public class ItemDto {
    private Long id;
    private String name;
    private String description;
    private Boolean available;
    private ItemRequest request;
  //  private List<Booking> nextBooking;
  //  private List<Booking> lastBooking;

    public ItemDto(Long id, String name, String description, Boolean available, ItemRequest request) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.available = available;
        this.request = request;
    }
//
//    public ItemDto(Long id, String name, String description, Boolean available,
//                   ItemRequest request, List<Booking> nextBooking, List<Booking> lastBooking) {
//        this.id = id;
//        this.name = name;
//        this.description = description;
//        this.available = available;
//        this.request = request;
//        this.nextBooking = nextBooking;
//        this.lastBooking = lastBooking;
//    }
}
