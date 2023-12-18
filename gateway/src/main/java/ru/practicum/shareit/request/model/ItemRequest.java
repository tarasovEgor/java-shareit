package ru.practicum.shareit.request.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

import ru.practicum.shareit.user.model.User;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ItemRequest {

    @NotNull(message = "Description is mandatory")
    private String description;

    private User requestor;

    private LocalDateTime created = LocalDateTime.now();

}
