package ru.practicum.shareit.request.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.user.model.User;

import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ItemRequest {

    @NotNull(message = "Description is mandatory")
    private String description;

    private User requestor;

    @FutureOrPresent
    private LocalDateTime created = LocalDateTime.now();

}
