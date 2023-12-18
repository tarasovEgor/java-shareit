package ru.practicum.shareit.user.dto;

import lombok.Data;

@Data
public class UserDto {
    private final Long id;
    private String name;
    private String email;

    public UserDto(Long id, String name, String email) {
        this.id = id;
        this.name = name;
        this.email = email;
    }
}
