package ru.practicum.shareit.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class BookingDto {

//    @Id
//    @GeneratedValue(strategy = GenerationType.AUTO)
//    private Long id;

    @NotNull(message = "Item id is mandatory")
    private Long itemId;

//    @FutureOrPresent
    private LocalDateTime start;

//    @Future
    private LocalDateTime end;

//    private Item item;
//    private User booker;
//    private BookingStatus status;

}
